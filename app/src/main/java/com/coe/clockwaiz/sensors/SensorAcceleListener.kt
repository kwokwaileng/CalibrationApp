package com.coe.clockwaiz.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.coe.clockwaiz.DbWorkerThread
import com.coe.clockwaiz.PositionOffsetPref
import com.coe.clockwaiz.Trip.TripObservable
import com.coe.clockwaiz.Trip.TripObserver
import com.coe.clockwaiz.database.CalibrationDatabase
import com.coe.clockwaiz.database.TripEntity
import java.util.*

class SensorAcceleListener(var context: Context) : SensorEventListener, TripObservable {


    private var sensorManager: SensorManager? = null
    var observerList = mutableListOf<TripObserver>()
    var positionOffsetPref: PositionOffsetPref = PositionOffsetPref()
    var normRawAccel = arrayOf(0.0f, 0.0f, 0.0f)
    var medianRawAccel = arrayOf(0.0f, 0.0f, 0.0f)
    private lateinit var csvTrip: StringBuilder
    private var calibrationDb: CalibrationDatabase? = null

    companion object {
        lateinit var mTripThread: DbWorkerThread
    }

    private fun initcsvString() {

        csvTrip = StringBuilder("millisecond,")
                .append("normalRawX,")
                .append("normalRawY,")
                .append("normalRawZ,")
                .append("medianRawAccelX,")
                .append("medianRawAccelY,")
                .append("medianRawAccelZ,")
                .append("\n")
    }

    init {
        mTripThread = DbWorkerThread("accelerationThread")
        mTripThread.start()
        calibrationDb = CalibrationDatabase.getInstance(context)
    }

    fun registerSensor() {
        initcsvString()
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER)?.size != 0) {

            val getSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (getSensor == null) {
                // showLog("Sensor Linear", "Device has no sensor", "Failed")
            } else {
                //showLog("Sensor Linear", "Start sensor", "Success")
                sensorManager?.registerListener(this, getSensor, SensorManager.SENSOR_DELAY_FASTEST)

            }

        }
    }

    override fun registerObserver(observer: TripObserver) {
        observerList.add(observer)
    }

    override fun removeObserver(observer: TripObserver) {
        observerList.remove(observer)
    }

    override fun notifyObserver(position: String) {
        for (observer in observerList) {
            observer.onFinishListening()
            clearAllData()
        }
    }

    private fun clearAllData() {
        normRawAccel = arrayOf(0.0f, 0.0f, 0.0f)
        medianRawAccel = arrayOf(0.0f, 0.0f, 0.0f)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                Log.d("SENSOR", "Sensor accuracy changed $accuracy")

            }

        }
    }

    private fun insertTripData(tripEntity: TripEntity) {

        val task = Runnable {
            calibrationDb?.sensorDataDao()?.insertTripData(tripEntity)
        }
        mTripThread.postTask(task)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                normRawAccel[0] = (event.values[0] - positionOffsetPref.xOffset) / (positionOffsetPref.xRefValueLandscapeLeft - positionOffsetPref.xOffset)
                normRawAccel[1] = (event.values[1] - positionOffsetPref.yOffset) / (positionOffsetPref.yRefValuePortraitUp - positionOffsetPref.yOffset)
                normRawAccel[2] = (event.values[2] - positionOffsetPref.zOffset) / (positionOffsetPref.zRefValueFront - positionOffsetPref.zOffset)

                medianRawAccel = getMedianNormaliseReading(normRawAccel)

                val tripEntity = TripEntity()
                tripEntity.millisecond = System.currentTimeMillis()
                tripEntity.normalRawX = normRawAccel[0]
                tripEntity.normalRawY = normRawAccel[1]
                tripEntity.normalRawZ = normRawAccel[2]
                tripEntity.medianRawAccelX = medianRawAccel[0]
                tripEntity.medianRawAccelY = medianRawAccel[1]
                tripEntity.medianRawAccelZ = medianRawAccel[2]

                insertTripData(tripEntity)

            }
        }
    }

    fun removeSensorListeners() {
        try {
            if (sensorManager != null) {
                sensorManager?.unregisterListener(this)
                notifyObserver("")
            }

        } catch (e: Exception) {

        }
    }


    private val speedArrayDeque: ArrayDeque<Float> = ArrayDeque()

    fun getMovingAverageSpeed(speed: Float): Float {

        if (speedArrayDeque.size == 10) {
            speedArrayDeque.poll()
        }

        speedArrayDeque.offer(speed)
        val speedArray: Array<Float> = speedArrayDeque.toTypedArray()

        return speedArray.average().toFloat()
    }


    val xReading: ArrayDeque<Float> = ArrayDeque()
    val yReading: ArrayDeque<Float> = ArrayDeque()
    val zReading: ArrayDeque<Float> = ArrayDeque()

    fun getMedianNormaliseReading(rawReading: Array<Float>): Array<Float> {

        val medianReading: Array<Float> = arrayOf(0.0f, 0.0f, 0.0f)

        if (xReading.size == 10) {
            xReading.poll()
        }


        if (yReading.size == 10) {
            yReading.poll()
        }

        if (zReading.size == 10) {
            zReading.poll()
        }

        xReading.offer(rawReading[0])
        Log.d("median", "XREADING :${xReading.joinToString()}")

        val xRawReading: Array<Float> = xReading.toTypedArray()
        xRawReading.sort()
        Log.d("median", "XRAWREADING SORT :${xRawReading.joinToString()}")


        yReading.offer(rawReading[1])
        val yRawReading: Array<Float> = yReading.toTypedArray()
        Log.d("median", "YREADING :${yReading.joinToString()}")
        yRawReading.sort()
        Log.d("median", "YRAWREADING SORT :${yRawReading.joinToString()}")

        zReading.offer(rawReading[2])
        val zRawReading: Array<Float> = zReading.toTypedArray()
        Log.d("median", "ZREADING :${zReading.joinToString()}")
        zRawReading.sort()
        Log.d("median", "ZRAWREADING SORT :${zRawReading.joinToString()}")


        medianReading[0] = getMedian(xRawReading)
        medianReading[1] = getMedian(yRawReading)
        medianReading[2] = getMedian(zRawReading)

        Log.d("median", " MEDIAN XYZ :${medianReading.joinToString()}")

        return medianReading

    }


    fun getMedian(rawReading: Array<Float>): Float = if (rawReading.size.rem(2) == 0) {
        val medianIndex = rawReading.size / 2
        (rawReading[medianIndex - 1] + rawReading[medianIndex]) / 2
    } else {
        val medianIndex = rawReading.size / 2
        rawReading[medianIndex]
    }


}