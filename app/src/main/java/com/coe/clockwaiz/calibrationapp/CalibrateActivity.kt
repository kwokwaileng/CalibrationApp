package com.coe.clockwaiz.calibrationapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.coe.clockwaiz.*
import com.coe.clockwaiz.sensors.SensorListener
import com.coe.clockwaiz.sensors.SensorObserver
import kotlinx.android.synthetic.main.activity_calibrate_front.*
import android.os.VibrationEffect
import android.os.Build
import android.os.Vibrator
import com.coe.clockwaiz.database.CalibrationDatabase


class CalibrateActivity : AppCompatActivity(), View.OnClickListener, SensorObserver {


    var positionOffsetPref: PositionOffsetPref = PositionOffsetPref()

    private lateinit var timer: CountDownTimer
    private lateinit var calibratetimer: CountDownTimer
    private lateinit var actionBar: ActionBar
    private var sensorListener: SensorListener? = null
    private var position: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate_front)
        position = intent?.getStringExtra("POSITION")!!

        actionBar = this.supportActionBar!!
        actionBar.title = "Calibrate $position"
        actionBar.setDisplayHomeAsUpEnabled(true)

        sensorListener = SensorListener(this)
        sensorListener?.registerObserver(this)
        initButton()
        initTimer()

    }

    private fun initButton() {
        btnStart.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStart -> {
                timer.start()
                btnStart.visibility = View.GONE


            }
        }
    }

    fun callToast() {
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
    }


    private fun initTimer() {

        timer = object : CountDownTimer(1000 * 5, 1000) {

            override fun onFinish() {
                callToast()
                txtTimer.text = getString(R.string.calibrating_start)
                sensorListener?.registerSensor(position)
                startCalibrateTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
                var seconds = (millisUntilFinished / 1000).toInt()
                val hours = seconds / (60 * 60)
                val tempMint = seconds - hours * 60 * 60
                val minutes = tempMint / 60
                seconds = tempMint - minutes * 60

                txtTimer.text = "Ready in ${seconds}s"
            }

        }


    }

    private fun startCalibrateTimer() {

        calibratetimer = object : CountDownTimer(1000 * 15, 1000) {

            override fun onFinish() {
                txtTimer.text = "Start Calibrating.... finish in 0s"
                sensorListener?.removeSensorListeners()
                btnStart.visibility = View.VISIBLE
                txtTimer.text = getString(R.string.calibration_end)
            }

            override fun onTick(millisUntilFinished: Long) {
                var seconds = (millisUntilFinished / 1000).toInt()
                val hours = seconds / (60 * 60)
                val tempMint = seconds - hours * 60 * 60
                val minutes = tempMint / 60
                seconds = tempMint - minutes * 60

                txtTimer.text = "Start Calibrating.... finish in ${seconds}s"
            }

        }.start()


    }

    override fun onStartListening() {

    }

    override fun onFinishListening(csvEvent: String, csvSummary: String) {
        Toast.makeText(this, "FINISH LISTENING GOT CSV DATA", Toast.LENGTH_SHORT).show()

        Log.d("sensor", "Summary csvEvent $csvEvent")
        Log.d("sensor", "Summary csvSummary $csvSummary")

        val intentInput = Intent(this, SaveFileService::class.java)
        intentInput.putExtra(Constants.POSITION, position)
        intentInput.putExtra(Constants.CSV_STRING, "")//csvEvent)
        intentInput.action = Constants.ACTION_SAVE_FILE_INPUT
        startService(intentInput)


        val intentSummary = Intent(this, SaveFileService::class.java)
        intentSummary.putExtra(Constants.POSITION, position)
        intentSummary.putExtra(Constants.CSV_STRING, csvSummary)
        intentSummary.action = Constants.ACTION_SAVE_FILE_SUMMARY
        startService(intentSummary)

        vibrate()

    }

    override fun onFinishData(xAverage: Double, yAverage: Double, zAverage: Double, position: String) {
        when (position) {
            Constants.POSITION_FRONT -> {
                positionOffsetPref.zRefValueFront = zAverage.toFloat()
                positionOffsetPref.xOffsetFront = xAverage.toFloat()
                positionOffsetPref.yOffsetFront = yAverage.toFloat()

            }

            Constants.POSITION_BACK -> {
                positionOffsetPref.zRefValueBack = zAverage.toFloat()
                positionOffsetPref.xOffsetBack = xAverage.toFloat()
                positionOffsetPref.yOffsetFront = yAverage.toFloat()
            }

            Constants.POSITION_PORTRAIT_UP -> {
                positionOffsetPref.yRefValuePortraitUp = yAverage.toFloat()
                positionOffsetPref.xOffsetPortraitUp = xAverage.toFloat()
                positionOffsetPref.zOffsetPortraitUp = zAverage.toFloat()
            }

            Constants.POSITION_PORTRAIT_DOWN -> {
                positionOffsetPref.yRefValuePortraitDown = yAverage.toFloat()
                positionOffsetPref.xOffsetPortraitDown = xAverage.toFloat()
                positionOffsetPref.zOffsetPortraitDown = zAverage.toFloat()

            }

            Constants.POSITION_LANDSCAPE_LEFT -> {
                positionOffsetPref.xRefValueLandscapeLeft = xAverage.toFloat()
                positionOffsetPref.yOffSetLandscapeLeft = yAverage.toFloat()
                positionOffsetPref.zOffSetLandscapeLeft = zAverage.toFloat()

            }
            Constants.POSITION_LANDSCAPE_RIGHT -> {
                positionOffsetPref.xRefValueLandscapeRight = xAverage.toFloat()
                positionOffsetPref.yOffsetLandscapeRight = yAverage.toFloat()
                positionOffsetPref.zOffsetLandscapeRight = yAverage.toFloat()

            }

        }
    }


    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(1500)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        SensorListener.mDbWorkerThread.quitSafely()
    }
}
