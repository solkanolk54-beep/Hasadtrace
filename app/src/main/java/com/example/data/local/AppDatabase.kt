package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.CropBatch
import com.example.data.model.Farm
import com.example.data.model.TraceEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms")
    fun getAllFarms(): Flow<List<Farm>>

    @Query("SELECT * FROM farms WHERE id = :farmId LIMIT 1")
    suspend fun getFarmById(farmId: String): Farm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: Farm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarms(farms: List<Farm>)
}

@Dao
interface CropBatchDao {
    @Query("SELECT * FROM crop_batches ORDER BY createdAt DESC")
    fun getAllBatches(): Flow<List<CropBatch>>

    @Query("SELECT * FROM crop_batches WHERE batchId = :batchId LIMIT 1")
    suspend fun getBatchById(batchId: String): CropBatch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: CropBatch)

    @Update
    suspend fun updateBatch(batch: CropBatch)

    @Query("UPDATE crop_batches SET scanCount = scanCount + 1 WHERE batchId = :batchId")
    suspend fun incrementScanCount(batchId: String)
}

@Dao
interface TraceEventDao {
    @Query("SELECT * FROM trace_events WHERE batchId = :batchId ORDER BY stepOrder ASC")
    fun getEventsForBatch(batchId: String): Flow<List<TraceEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TraceEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<TraceEvent>)
}

@Database(
    entities = [Farm::class, CropBatch::class, TraceEvent::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun cropBatchDao(): CropBatchDao
    abstract fun traceEventDao(): TraceEventDao
}
