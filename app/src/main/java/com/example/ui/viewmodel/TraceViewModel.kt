package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CropBatch
import com.example.data.model.Farm
import com.example.data.model.TraceEvent
import com.example.data.repository.TraceabilityRepository
import com.example.security.CryptoTraceHelper
import com.example.security.SecurityAudit
import com.example.security.VerificationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab {
    CONSUMER_PASSPORT,
    FARMER_PORTAL,
    SUPPLY_CHAIN_LOGS,
    SYSTEM_ARCHITECTURE
}

class TraceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TraceabilityRepository(application)

    val allBatches: StateFlow<List<CropBatch>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allFarms: StateFlow<List<Farm>> = repository.allFarms
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentTab = MutableStateFlow(AppNavTab.CONSUMER_PASSPORT)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _selectedBatch = MutableStateFlow<CropBatch?>(null)
    val selectedBatch: StateFlow<CropBatch?> = _selectedBatch.asStateFlow()

    private val _selectedFarm = MutableStateFlow<Farm?>(null)
    val selectedFarm: StateFlow<Farm?> = _selectedFarm.asStateFlow()

    private val _selectedBatchEvents = MutableStateFlow<List<TraceEvent>>(emptyList())
    val selectedBatchEvents: StateFlow<List<TraceEvent>> = _selectedBatchEvents.asStateFlow()

    private val _securityAudit = MutableStateFlow<SecurityAudit?>(null)
    val securityAudit: StateFlow<SecurityAudit?> = _securityAudit.asStateFlow()

    private val _isSignatureValid = MutableStateFlow(true)
    val isSignatureValid: StateFlow<Boolean> = _isSignatureValid.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            // Select default first batch
            allBatches.collect { batches ->
                if (batches.isNotEmpty() && _selectedBatch.value == null) {
                    selectBatch(batches.first().batchId)
                }
            }
        }
    }

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun selectBatch(batchId: String) {
        viewModelScope.launch {
            val batch = repository.getBatchById(batchId)
            if (batch != null) {
                _selectedBatch.value = batch
                _selectedFarm.value = repository.getFarmById(batch.farmId)

                // Verify signature
                val isValid = CryptoTraceHelper.verifyBatchSignature(
                    batch.batchId,
                    batch.farmId,
                    batch.harvestDate.substringBefore(" "),
                    batch.cropName,
                    batch.hmacSignature
                )
                _isSignatureValid.value = isValid

                // Evaluate scan security status
                _securityAudit.value = CryptoTraceHelper.evaluateSecurityStatus(batch.scanCount)

                // Collect events
                repository.getEventsForBatch(batchId).collect { events ->
                    _selectedBatchEvents.value = events
                }
            }
        }
    }

    fun scanQrCode(rawCodeOrUrl: String) {
        viewModelScope.launch {
            // Extract batchId from URL or raw string
            val batchId = when {
                rawCodeOrUrl.contains("bid=") -> {
                    rawCodeOrUrl.substringAfter("bid=").substringBefore("&")
                }
                rawCodeOrUrl.contains("BATCH-") -> {
                    // Extract BATCH-... pattern
                    val start = rawCodeOrUrl.indexOf("BATCH-")
                    val sub = rawCodeOrUrl.substring(start)
                    val end = sub.indexOfFirst { it == '&' || it == ' ' || it == '?' || it == '\n' }
                    if (end != -1) sub.substring(0, end) else sub
                }
                else -> rawCodeOrUrl.trim()
            }

            val batch = repository.getBatchById(batchId)
            if (batch != null) {
                repository.incrementScanCount(batchId)
                selectBatch(batchId)
                _userMessage.value = "تم مسح كود الـ QR والتحقق من الهوية الرقمية للدفعة بنجاح!"
                _currentTab.value = AppNavTab.CONSUMER_PASSPORT
            } else {
                _userMessage.value = "رمز الـ QR غير مسجل في قاعدة البيانات، يرجى التأكد من المصدر."
            }
        }
    }

    fun createNewBatch(
        farmId: String,
        cropName: String,
        cropCategory: String,
        variety: String,
        quantityDesc: String,
        harvestDate: String,
        fieldPlot: String,
        farmingMethod: String,
        irrigationDetails: String,
        fertilizerUsed: String,
        pestControl: String,
        qualityGrade: String,
        labTestStatus: String,
        labReportNumber: String,
        sugarBrix: String
    ) {
        viewModelScope.launch {
            // Generate clean unique Batch ID
            val randomSuffix = (1000..9999).random()
            val cleanDate = harvestDate.substringBefore(" ").replace("-", "")
            val batchId = "BATCH-${farmId.takeLast(4)}-$cleanDate-$randomSuffix"

            // Compute HMAC-SHA256 signature
            val hmacSig = CryptoTraceHelper.generateBatchSignature(
                batchId,
                farmId,
                harvestDate.substringBefore(" "),
                cropName
            )

            val newBatch = CropBatch(
                batchId = batchId,
                farmId = farmId,
                cropName = cropName,
                cropCategory = cropCategory,
                variety = variety,
                quantityDesc = quantityDesc,
                harvestDate = harvestDate,
                fieldPlot = fieldPlot,
                farmingMethod = farmingMethod,
                irrigationDetails = irrigationDetails,
                fertilizerUsed = fertilizerUsed,
                pestControl = pestControl,
                hmacSignature = hmacSig,
                currentStage = "HARVESTED",
                qualityGrade = qualityGrade,
                labTestStatus = labTestStatus,
                labReportNumber = labReportNumber,
                sugarBrix = sugarBrix,
                scanCount = 1
            )

            repository.insertBatch(newBatch)

            // Insert initial harvest event
            val initialEvent = TraceEvent(
                batchId = batchId,
                stepOrder = 1,
                stageName = "الجني والقطف وتسجيل الدفعة",
                location = "المزرعة - $fieldPlot",
                timestamp = harvestDate,
                operatorName = "مدير الإنتاج بالمزرعة",
                details = "تم تسجيل بيانات الحصاد والري والتربة وتوليد رمز QR الرقمي بتوقيع تشفيري HMAC-SHA256.",
                temperatureRecorded = "22.0°C"
            )
            repository.insertEvent(initialEvent)

            // Select and switch to passport
            selectBatch(batchId)
            _userMessage.value = "تم إنشاء الدفعة $batchId وتوليد رمز الـ QR الرقمي بنجاح!"
            _currentTab.value = AppNavTab.CONSUMER_PASSPORT
        }
    }

    fun addCheckpointEvent(
        batchId: String,
        stageName: String,
        location: String,
        operatorName: String,
        details: String,
        temperature: String?
    ) {
        viewModelScope.launch {
            val currentEvents = _selectedBatchEvents.value
            val nextStep = (currentEvents.maxOfOrNull { it.stepOrder } ?: 0) + 1

            val newEvent = TraceEvent(
                batchId = batchId,
                stepOrder = nextStep,
                stageName = stageName,
                location = location,
                timestamp = "الآن - سبتمبر 2026",
                operatorName = operatorName,
                details = details,
                temperatureRecorded = temperature
            )
            repository.insertEvent(newEvent)

            // Update batch current stage
            val batch = repository.getBatchById(batchId)
            if (batch != null) {
                val updatedStage = when {
                    stageName.contains("تعبئة") || stageName.contains("تغليف") -> "PACKAGED"
                    stageName.contains("شحن") || stageName.contains("نقل") -> "IN_TRANSIT"
                    stageName.contains("استلام") || stageName.contains("بيع") -> "DELIVERED"
                    else -> batch.currentStage
                }
                repository.updateBatch(batch.copy(currentStage = updatedStage))
                selectBatch(batchId)
            }

            _userMessage.value = "تمت إضافة محطة التتبع الجديدة بنجاح!"
        }
    }
}
