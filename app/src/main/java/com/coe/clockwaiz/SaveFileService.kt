package com.coe.clockwaiz

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.coe.clockwaiz.database.CalibrationDatabase

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    lateinit var  param1:String
    lateinit var  param2:String
    private var calibrationDb: CalibrationDatabase? = null

class SaveFileService : IntentService("SaveFileService") {

    override fun onCreate() {
        super.onCreate()
        calibrationDb = CalibrationDatabase.getInstance(this)
    }

    override fun onHandleIntent(intent: Intent?) {

        val utility = Utility()
        param1 = intent?.getStringExtra(Constants.POSITION)!!
        param2 = intent?.getStringExtra(Constants.CSV_STRING)!!

        when (intent.action) {
            Constants.ACTION_SAVE_FILE_INPUT -> {
               // utility.writeToCsvInput(this,param1,param2)

                utility.writeToCsvInput(this,param1,getAllSensorData(param1))
                calibrationDb?.sensorDataDao()?.deleteAllSensorData()
            }
            Constants.ACTION_SAVE_FILE_SUMMARY -> {
                utility.writeToCsvSummary(this,param1,param2)
            }
            Constants.ACTION_SAVE_FILE_TRIP -> {
                utility.writeToCsvTrip(this,getAllTripData())
                calibrationDb?.sensorDataDao()?.deleteAllTripData()

            }
        }
    }


    private fun getAllTripData():String{
        var csvTrip :StringBuilder? = null
        val tripArray = calibrationDb?.sensorDataDao()?.getAllTripData()
        csvTrip = StringBuilder("millisecond,")
                .append("normalRawX,")
                .append("normalRawY,")
                .append("normalRawZ,")
                .append("medianRawAccelX,")
                .append("medianRawAccelY,")
                .append("medianRawAccelZ,")
                .append("\n")

        tripArray?.forEach {
            csvTrip?.append(it.millisecond)
                    ?.append(",")?.append(it.normalRawX)
                    ?.append(",")?.append(it.normalRawY)
                    ?.append(",")?.append(it.normalRawZ)
                    ?.append(",")?.append(it.medianRawAccelX)
                    ?.append(",")?.append(it.medianRawAccelY)
                    ?.append(",")?.append(it.medianRawAccelZ)
                    ?.append(",")?.append("\n")
        }

        return csvTrip.toString()
    }


    private fun getAllSensorData(position:String):String{
        var csvString :StringBuilder? = null
        val sensorArray = calibrationDb?.sensorDataDao()?.getAllSensorData()

        when (position) {

            Constants.POSITION_FRONT -> {
                csvString = StringBuilder("miliseconds,")
                        .append("z_ref_value_front,")
                        .append("x_offset_front,")
                        .append("y_offset_front,")
                        .append("\n")

            }

            Constants.POSITION_BACK -> {
                csvString = StringBuilder("miliseconds,")
                        .append("z_ref_value_back,")
                        .append("x_offset_back,")
                        .append("y_offset_back,")
                        .append("\n")


            }

            Constants.POSITION_PORTRAIT_UP -> {
                csvString = StringBuilder("miliseconds,")
                        .append("y_ref_value_portrait_up,")
                        .append("x_offset_portrait_up,")
                        .append("z_offset_portrait_up,")
                        .append("\n")
            }

            Constants.POSITION_PORTRAIT_DOWN -> {
                csvString = StringBuilder("miliseconds,")
                        .append("y_ref_value_portrait_down,")
                        .append("x_offset_portrait_down,")
                        .append("z_offset_portrait_down,")
                        .append("\n")
            }

            Constants.POSITION_LANDSCAPE_LEFT -> {
                csvString = StringBuilder("miliseconds,")
                        .append("x_ref_value_landscape_left,")
                        .append("y_offset_landscape_left,")
                        .append("z_offset_landscape_left,")
                        .append("\n")
            }
            Constants.POSITION_LANDSCAPE_RIGHT -> {
                csvString = StringBuilder("miliseconds,")
                        .append("x_ref_value_landscape_right,")
                        .append("y_offset_landscape_right,")
                        .append("z_offset_landscape_right,")
                        .append("\n")

            }
        }


        Log.d("sensor","SENSOR SIZE : ${sensorArray?.size}")
        sensorArray?.forEach {
            csvString?.append(it.millisecond)
                    ?.append(",")?.append(it.xData)
                    ?.append(",")?.append(it.yData)
                    ?.append(",")?.append(it.zData)
                    ?.append(",")?.append("\n")
        }

        return csvString.toString()

    }


    override fun onDestroy() {
        super.onDestroy()
        CalibrationDatabase.destroyInstance()
    }
}
