package com.coe.clockwaiz.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CalibrationDataDao {

    @Query("SELECT * from SensorData")
    fun getAllSensorData(): List<SensorEntity>

    @Insert(onConflict = REPLACE)
    fun insertSensorData(sensorEntity: SensorEntity)

    @Query("DELETE from SensorData")
    fun deleteAllSensorData()

    @Query("SELECT * from TripData")
    fun getAllTripData(): List<TripEntity>

    @Insert(onConflict = REPLACE)
    fun insertTripData(tripEntity: TripEntity)

    @Query("DELETE from TripData")
    fun deleteAllTripData()

}