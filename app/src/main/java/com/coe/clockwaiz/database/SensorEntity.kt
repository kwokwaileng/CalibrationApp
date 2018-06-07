package com.coe.clockwaiz.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "SensorData")
data class SensorEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                        @ColumnInfo(name = "millisecond") var millisecond:Long,
                        @ColumnInfo(name = "xData") var xData: Float,
                        @ColumnInfo(name = "yData") var yData: Float,
                        @ColumnInfo(name = "zData") var zData: Float){

    constructor():this(null,0,0.0f,0.0f,0.0f)
}


@Entity(tableName = "TripData")
data class TripEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                        @ColumnInfo(name = "millisecond") var millisecond:Long,
                        @ColumnInfo(name = "normalRawX") var normalRawX: Float,
                        @ColumnInfo(name = "normalRawY") var normalRawY: Float,
                        @ColumnInfo(name = "normalRawZ") var normalRawZ: Float,
                        @ColumnInfo(name = "medianRawAccelX") var medianRawAccelX: Float,
                        @ColumnInfo(name = "medianRawAccelY") var medianRawAccelY: Float,
                        @ColumnInfo(name = "medianRawAccelZ") var medianRawAccelZ: Float){

    constructor():this(null,0,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f)
}

