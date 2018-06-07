package com.coe.clockwaiz.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = [(SensorEntity::class),(TripEntity::class)], version = 2)
abstract class CalibrationDatabase: RoomDatabase() {

    abstract fun sensorDataDao(): CalibrationDataDao

    companion object {
        private var INSTANCE: CalibrationDatabase? = null

        fun getInstance(context: Context): CalibrationDatabase? {
            if (INSTANCE == null) {
                synchronized(CalibrationDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            CalibrationDatabase::class.java, "sensor.db")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}