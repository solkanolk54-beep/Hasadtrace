package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class Farm(
    @PrimaryKey val id: String,
    val name: String,
    val ownerName: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val irrigationType: String,
    val soilType: String,
    val organicCertNumber: String,
    val certBody: String,
    val areaHectares: Double,
    val establishedYear: Int,
    val phoneContact: String
)

@Entity(tableName = "crop_batches")
data class CropBatch(
    @PrimaryKey val batchId: String,
    val farmId: String,
    val cropName: String,
    val cropCategory: String, // تمور، خضار، فواكه، زيتون
    val variety: String,
    val quantityDesc: String,
    val harvestDate: String,
    val fieldPlot: String,
    val farmingMethod: String,
    val irrigationDetails: String,
    val fertilizerUsed: String,
    val pestControl: String,
    val hmacSignature: String,
    val currentStage: String, // HARVESTED, PACKAGED, IN_TRANSIT, DELIVERED
    val qualityGrade: String,
    val labTestStatus: String,
    val labReportNumber: String,
    val sugarBrix: String,
    val scanCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trace_events")
data class TraceEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val batchId: String,
    val stepOrder: Int,
    val stageName: String,
    val location: String,
    val timestamp: String,
    val operatorName: String,
    val details: String,
    val temperatureRecorded: String? = null,
    val isVerified: Boolean = true
)
