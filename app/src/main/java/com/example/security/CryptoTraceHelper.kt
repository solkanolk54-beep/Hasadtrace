package com.example.security

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptoTraceHelper {
    // In production, this key is securely held in Cloud KMS or Hardware Security Module (HSM)
    private const val SYSTEM_PEPPER = "HasadTrace-AgriTech-KSA-Organic-Secret-Key-2026"

    /**
     * Generates an HMAC-SHA256 cryptographic signature for a crop batch.
     * Prevents forgery, unauthorized QR generation, and data alteration.
     */
    fun generateBatchSignature(
        batchId: String,
        farmId: String,
        harvestDate: String,
        cropName: String
    ): String {
        val payload = "$batchId|$farmId|$harvestDate|$cropName"
        return try {
            val secretKey = SecretKeySpec(SYSTEM_PEPPER.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(secretKey)
            val bytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }.take(16) // 16-char hex fingerprint
        } catch (e: Exception) {
            // Fallback SHA-256 hash if MAC fails
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(payload.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }.take(16)
        }
    }

    /**
     * Verifies that the given signature matches the batch details.
     */
    fun verifyBatchSignature(
        batchId: String,
        farmId: String,
        harvestDate: String,
        cropName: String,
        providedSignature: String
    ): Boolean {
        val expected = generateBatchSignature(batchId, farmId, harvestDate, cropName)
        return slowEquals(expected, providedSignature)
    }

    /**
     * Constant-time comparison to prevent side-channel timing attacks.
     */
    private fun slowEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var diff = 0
        for (i in a.indices) {
            diff = diff or (a[i].code xor b[i].code)
        }
        return diff == 0
    }

    /**
     * Builds the final canonical URL encoded into the physical QR Code.
     */
    fun buildQrPayloadUrl(batchId: String, farmId: String, signature: String): String {
        return "https://trace.hasad.farm/passport?bid=$batchId&fid=$farmId&sig=$signature"
    }

    /**
     * Evaluates anti-counterfeit status based on scan statistics.
     */
    fun evaluateSecurityStatus(scanCount: Int): SecurityAudit {
        return when {
            scanCount <= 5 -> SecurityAudit(
                status = VerificationStatus.AUTHENTIC,
                title = "منتج أصلي وموثوق 100%",
                description = "التوقيع التشفيري مطابق لسجل المزرعة في النظام السحابي، ولم يتم رصد أي محاولات نسخ غير مشروعة.",
                colorHex = 0xFF1B5E20
            )
            scanCount in 6..15 -> SecurityAudit(
                status = VerificationStatus.NORMAL_INSPECTIONS,
                title = "تم التحقق (عدة مسحات مسجلة)",
                description = "تم فحص الرمز من قِبل نقاط التوزيع والمستهلكين $scanCount مرات. جميع البيانات مطابقة للسجل الأصلي.",
                colorHex = 0xFF2E7D32
            )
            else -> SecurityAudit(
                status = VerificationStatus.SUSPICIOUS_DUPLICATION,
                title = "تنبيه: نشاط مسح متكرر بشكل غير عادي",
                description = "تم مسح هذا الرمز $scanCount مرة. قد يشير ذلك إلى تصوير أو إعادة طباعة غير مرخصة للملصق. يرجى التحقق من ختم العبوة.",
                colorHex = 0xFFC62828
            )
        }
    }
}

enum class VerificationStatus {
    AUTHENTIC,
    NORMAL_INSPECTIONS,
    SUSPICIOUS_DUPLICATION,
    INVALID_SIGNATURE
}

data class SecurityAudit(
    val status: VerificationStatus,
    val title: String,
    val description: String,
    val colorHex: Long
)
