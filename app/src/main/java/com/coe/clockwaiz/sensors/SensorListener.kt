package com.coe.clockwaiz.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.coe.clockwaiz.Constants
import com.coe.clockwaiz.DbWorkerThread
import com.coe.clockwaiz.database.CalibrationDatabase
import com.coe.clockwaiz.database.SensorEntity


class SensorListener(var context: Context) : SensorEventListener, SensorObservable {


    private lateinit var csvString: StringBuilder
    private lateinit var csvSummary: StringBuilder
    private var sensorManager: SensorManager? = null
    private var position: String = ""

    private var ZArrayValue: ArrayList<Float> = ArrayList()
    private var XArrayValue: ArrayList<Float> = ArrayList()
    private var YArrayValue: ArrayList<Float> = ArrayList()

    private var calibrationDb: CalibrationDatabase? = null


    companion object {
         lateinit var mDbWorkerThread: DbWorkerThread
    }

    init {
        calibrationDb = CalibrationDatabase.getInstance(context)
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
    }

    var observerList = mutableListOf<SensorObserver>()

    private fun initcsvString(position: String) {

        csvSummary = StringBuilder("variable,")
                .append("value,")
                .append("\n")


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

    }

    fun registerSensor(position: String) {
        this.position = position
        initcsvString(position)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


        if (sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER)?.size != 0) {

            val getSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (getSensor == null) {
                // showLog("Sensor Linear", "Device has no sensor", "Failed")
            } else {
                //showLog("Sensor Linear", "Start sensor", "Success")
                sensorManager?.registerListener(this, getSensor, SensorManager.SENSOR_DELAY_UI)



            }
        }

    }

    private fun insertSensorData(sensorEntity: SensorEntity) {

        val task = Runnable {
            val a = calibrationDb?.sensorDataDao()?.insertSensorData(sensorEntity)
            Log.d("LOG", "INSERT SENSOR DATA $a")
        }
        mDbWorkerThread.postTask(task)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                Log.d("SENSOR", "Sensor accuracy changed $accuracy")

            }

        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                Log.d("SENSOR", "Reading ${event.values.joinToString(prefix = "{", postfix = "}")}")

                ZArrayValue.add(event.values[2])
                XArrayValue.add(event.values[0])
                YArrayValue.add(event.values[1])


                var sensorEntity = SensorEntity()
                sensorEntity.millisecond = System.currentTimeMillis()
                sensorEntity.xData = event.values[0]
                sensorEntity.yData = event.values[1]
                sensorEntity.zData = event.values[2]

                insertSensorData(sensorEntity)


                when (position) {
                    Constants.POSITION_FRONT, Constants.POSITION_BACK -> {
                        csvString.append(System.currentTimeMillis())
                                .append(",").append(event.values[2])
                                .append(",").append(event.values[0])
                                .append(",").append(event.values[1])
                                .append(",").append("\n")
                    }

                    Constants.POSITION_PORTRAIT_UP, Constants.POSITION_PORTRAIT_DOWN -> {
                        csvString.append(System.currentTimeMillis())
                                .append(",").append(event.values[1])
                                .append(",").append(event.values[0])
                                .append(",").append(event.values[2])
                                .append(",").append("\n")
                    }

                    Constants.POSITION_LANDSCAPE_RIGHT, Constants.POSITION_LANDSCAPE_LEFT -> {
                        csvString.append(System.currentTimeMillis())
                                .append(",").append(event.values[0])
                                .append(",").append(event.values[1])
                                .append(",").append(event.values[2])
                                .append(",").append("\n")
                    }
                }
            }


        }
    }

    override fun registerObserver(observer: SensorObserver) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: SensorObserver) {
        observerList.remove(observer)
    }

    override fun notifyObserver(position: String) {

        for (observer in observerList) {
            observer.onFinishListening(csvString.toString(), getCsvSummary(position))
            observer.onFinishData(XArrayValue.average(), YArrayValue.average(), ZArrayValue.average(), position)
            clearAllData()
        }

    }

    private fun clearAllData() {
        XArrayValue.clear()
        YArrayValue.clear()
        ZArrayValue.clear()
    }


    private fun getCsvSummary(position: String): String {

        when (position) {
            Constants.POSITION_FRONT -> {
                csvSummary.append("z_ref_value_front")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")

                        .append("x_offset_front")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("y_offset_front")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")

            }
            Constants.POSITION_BACK -> {
                csvSummary.append("z_ref_value_back")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")

                        .append("x_offset_back")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("y_offset_back")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")


            }

            Constants.POSITION_PORTRAIT_UP -> {
                csvSummary.append("y_ref_value_portrait_up")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")

                        .append("x_offset_portrait_up")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("z_offset_portrait_up")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")
            }

            Constants.POSITION_PORTRAIT_DOWN -> {
                csvSummary.append("y_ref_value_portrait_down")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")

                        .append("x_offset_portrait_down")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("z_offset_portrait_down")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")
            }

            Constants.POSITION_LANDSCAPE_LEFT -> {
                csvSummary.append("x_ref_value_landscape_left")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("y_offset_landscape_left")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")

                        .append("z_offset_landscape_left")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")
            }
            Constants.POSITION_LANDSCAPE_RIGHT -> {
                csvSummary.append("x_ref_value_landscape_right")
                        .append(",").append(XArrayValue.average())
                        .append(",").append("\n")

                        .append("y_offset_landscape_right")
                        .append(",").append(YArrayValue.average())
                        .append(",").append("\n")

                        .append("z_offset_landscape_right")
                        .append(",").append(ZArrayValue.average())
                        .append(",").append("\n")

            }

        }


        return csvSummary.toString()
    }


    fun removeSensorListeners() {
        try {
            if (sensorManager != null) {
                sensorManager?.unregisterListener(this)
                notifyObserver(position)
                //CalibrationDatabase.destroyInstance()
                //mDbWorkerThread.quit()
            }

        } catch (e: Exception) {

        }
    }

}