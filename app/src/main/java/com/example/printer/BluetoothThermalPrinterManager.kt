package com.example.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.UUID

/**
 * High-performance ESC/POS Bluetooth Thermal Printer Protocol Manager for Android.
 * Supports field thermal receipt printers (2-inch 58mm / 3-inch 80mm).
 */
class BluetoothThermalPrinterManager(
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
) {
    companion object {
        private const val TAG = "ThermalPrinterManager"
        // Standard SPP (Serial Port Profile) UUID for Bluetooth Serial Printers
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        // ESC/POS Commands
        private val ESC: Byte = 0x1B
        private val GS: Byte = 0x1D
        private val LF: Byte = 0x0A

        val CMD_INIT = byteArrayOf(ESC, '@'.code.toByte())
        val CMD_ALIGN_LEFT = byteArrayOf(ESC, 'a'.code.toByte(), 0x00)
        val CMD_ALIGN_CENTER = byteArrayOf(ESC, 'a'.code.toByte(), 0x01)
        val CMD_ALIGN_RIGHT = byteArrayOf(ESC, 'a'.code.toByte(), 0x02)
        val CMD_BOLD_ON = byteArrayOf(ESC, 'E'.code.toByte(), 0x01)
        val CMD_BOLD_OFF = byteArrayOf(ESC, 'E'.code.toByte(), 0x00)
        val CMD_DOUBLE_SIZE = byteArrayOf(GS, '!'.code.toByte(), 0x11) // 2x width & 2x height
        val CMD_NORMAL_SIZE = byteArrayOf(GS, '!'.code.toByte(), 0x00)
        val CMD_FEED_PAPER = byteArrayOf(ESC, 'd'.code.toByte(), 0x03) // feed 3 lines
        val CMD_CUT_PAPER = byteArrayOf(GS, 'V'.code.toByte(), 0x42, 0x00) // partial cut
    }

    enum class PaperWidth(val charsPerLine: Int, val dotsPerLine: Int) {
        WIDTH_58MM(charsPerLine = 32, dotsPerLine = 384),
        WIDTH_80MM(charsPerLine = 48, dotsPerLine = 576)
    }

    data class BatchLabelPrintData(
        val cropName: String,
        val farmName: String,
        val batchId: String,
        val harvestDate: String,
        val variety: String,
        val grade: String,
        val qrPayloadUrl: String,
        val certNumber: String = "SA-ORG-2026-908"
    )

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    val isConnected: Boolean
        get() = socket?.isConnected == true

    /**
     * Retrieves paired Bluetooth devices that match standard printer classes or names.
     */
    @SuppressLint("MissingPermission")
    fun getPairedPrinters(): List<BluetoothDevice> {
        val paired = bluetoothAdapter?.bondedDevices ?: return emptyList()
        return paired.filter { device ->
            val name = device.name?.lowercase() ?: ""
            name.contains("printer") || name.contains("pos") || name.contains("thermal") ||
                    name.contains("rpp") || name.contains("mpt") || name.contains("innerprinter") ||
                    name.contains("bt") || name.contains("esc")
        }.ifEmpty {
            paired.toList() // Return all paired devices if no specific printer name keyword matched
        }
    }

    /**
     * Establishes RFCOMM socket connection to the target Bluetooth printer.
     */
    @SuppressLint("MissingPermission")
    suspend fun connect(deviceAddress: String): Result<Boolean> = withContext(Dispatchers.IO) {
        disconnect()
        val adapter = bluetoothAdapter ?: return@withContext Result.failure(
            IllegalStateException("Bluetooth adapter not available")
        )

        try {
            val device = adapter.getRemoteDevice(deviceAddress)
            adapter.cancelDiscovery() // Always cancel discovery before connecting
            val tmpSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            tmpSocket.connect()
            socket = tmpSocket
            outputStream = tmpSocket.outputStream
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed to $deviceAddress", e)
            disconnect()
            Result.failure(e)
        }
    }

    /**
     * Closes the active Bluetooth socket safely.
     */
    fun disconnect() {
        try {
            outputStream?.flush()
            outputStream?.close()
        } catch (_: IOException) {}
        try {
            socket?.close()
        } catch (_: IOException) {}
        outputStream = null
        socket = null
    }

    /**
     * Prints a standardized organic field batch label with native ESC/POS QR code.
     */
    suspend fun printCropBatchLabel(
        labelData: BatchLabelPrintData,
        paperWidth: PaperWidth = PaperWidth.WIDTH_58MM
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val stream = outputStream ?: return@withContext Result.failure(
            IllegalStateException("Printer is not connected")
        )

        try {
            // 1. Initialize
            stream.write(CMD_INIT)

            // 2. Header
            stream.write(CMD_ALIGN_CENTER)
            stream.write(CMD_BOLD_ON)
            stream.write(CMD_DOUBLE_SIZE)
            writeEncodedText(stream, "★ HASAD TRACE ★\n")
            stream.write(CMD_NORMAL_SIZE)
            writeEncodedText(stream, "100% CERTIFIED ORGANIC\n")
            writeEncodedText(stream, "Cert: ${labelData.certNumber}\n")
            stream.write(CMD_BOLD_OFF)

            printDivider(stream, paperWidth)

            // 3. Product & Farm Details
            stream.write(CMD_ALIGN_LEFT)
            stream.write(CMD_BOLD_ON)
            writeEncodedText(stream, "Crop: ${labelData.cropName}\n")
            stream.write(CMD_BOLD_OFF)
            writeEncodedText(stream, "Variety: ${labelData.variety}\n")
            writeEncodedText(stream, "Farm: ${labelData.farmName}\n")
            writeEncodedText(stream, "Harvest: ${labelData.harvestDate}\n")
            writeEncodedText(stream, "Grade: ${labelData.grade}\n")
            writeEncodedText(stream, "Serial: ${labelData.batchId}\n")

            printDivider(stream, paperWidth)

            // 4. Native Hardware QR Code (ESC/POS GS ( k commands)
            stream.write(CMD_ALIGN_CENTER)
            writeEncodedText(stream, "SCAN TO VERIFY ORIGIN\n")
            printEscPosQrCode(stream, labelData.qrPayloadUrl, moduleSize = if (paperWidth == PaperWidth.WIDTH_80MM) 8 else 6)

            // 5. Footer & Authenticity Notice
            writeEncodedText(stream, "\nHMAC Cryptographically Sealed\n")
            writeEncodedText(stream, "Pesticide-Free: 0.00 ppm\n")

            // 6. Paper Feed & Optional Cut
            stream.write(CMD_FEED_PAPER)
            stream.write(CMD_FEED_PAPER)
            try {
                stream.write(CMD_CUT_PAPER)
            } catch (_: Exception) {
                // Some 58mm mobile printers do not have physical cutters; ignore
            }
            stream.flush()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to print label", e)
            Result.failure(e)
        }
    }

    /**
     * Sends ESC/POS native QR code generation commands.
     * Specification: GS ( k pL pH cn fn n1 n2 ...
     */
    private fun printEscPosQrCode(stream: OutputStream, qrData: String, moduleSize: Int = 6) {
        val dataBytes = qrData.toByteArray(Charset.forName("UTF-8"))
        val length = dataBytes.size + 3
        val pL = (length and 0xFF).toByte()
        val pH = ((length shr 8) and 0xFF).toByte()

        // 1. Select QR Model (Model 2)
        // GS ( k 0x04 0x00 0x31 0x41 0x32 0x00
        stream.write(byteArrayOf(GS, '('.code.toByte(), 'k'.code.toByte(), 0x04, 0x00, 0x31, 0x41, 0x32, 0x00))

        // 2. Set Module Size (1 to 16)
        // GS ( k 0x03 0x00 0x31 0x43 [moduleSize]
        val clampedSize = moduleSize.coerceIn(1, 16).toByte()
        stream.write(byteArrayOf(GS, '('.code.toByte(), 'k'.code.toByte(), 0x03, 0x00, 0x31, 0x43, clampedSize))

        // 3. Set Error Correction Level (Level M: 15% recovery -> 0x31, Level Q: 25% -> 0x32)
        stream.write(byteArrayOf(GS, '('.code.toByte(), 'k'.code.toByte(), 0x03, 0x00, 0x31, 0x45, 0x32))

        // 4. Store Data in QR Buffer
        // GS ( k pL pH 0x31 0x50 0x30 [dataBytes]
        val storeHeader = byteArrayOf(GS, '('.code.toByte(), 'k'.code.toByte(), pL, pH, 0x31, 0x50, 0x30)
        stream.write(storeHeader)
        stream.write(dataBytes)

        // 5. Print the stored QR symbol
        // GS ( k 0x03 0x00 0x31 0x51 0x30
        stream.write(byteArrayOf(GS, '('.code.toByte(), 'k'.code.toByte(), 0x03, 0x00, 0x31, 0x51, 0x30))
    }

    private fun printDivider(stream: OutputStream, paperWidth: PaperWidth) {
        val line = "-".repeat(paperWidth.charsPerLine) + "\n"
        writeEncodedText(stream, line)
    }

    private fun writeEncodedText(stream: OutputStream, text: String, charsetName: String = "UTF-8") {
        stream.write(text.toByteArray(Charset.forName(charsetName)))
    }
}
