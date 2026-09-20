package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hasad Trace", appName)
  }

  @Test
  fun `verify hmac signature generation and validation`() {
    val batchId = "BATCH-2026-TEST-001"
    val farmId = "FARM-AHS-01"
    val harvestDate = "2026-09-20"
    val cropName = "تمور خلاص عضوية"

    val signature = com.example.security.CryptoTraceHelper.generateBatchSignature(
      batchId, farmId, harvestDate, cropName
    )
    assertTrue(signature.isNotEmpty())

    val isValid = com.example.security.CryptoTraceHelper.verifyBatchSignature(
      batchId, farmId, harvestDate, cropName, signature
    )
    assertTrue(isValid)

    val isInvalid = com.example.security.CryptoTraceHelper.verifyBatchSignature(
      batchId, farmId, harvestDate, "منتج مزيف", signature
    )
    assertFalse(isInvalid)
  }
}
