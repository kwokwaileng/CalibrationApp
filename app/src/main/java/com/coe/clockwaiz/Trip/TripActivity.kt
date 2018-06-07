package com.coe.clockwaiz.Trip

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.coe.clockwaiz.Constants
import com.coe.clockwaiz.SaveFileService
import com.coe.clockwaiz.calibrationapp.R
import com.coe.clockwaiz.database.CalibrationDatabase
import com.coe.clockwaiz.sensors.SensorAcceleListener
import com.coe.clockwaiz.sensors.SensorListener
import kotlinx.android.synthetic.main.activity_trip.*

class TripActivity : AppCompatActivity(), View.OnClickListener,TripObserver{

    private var tripListener: SensorAcceleListener? = null

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.btnTrip -> {
                if(btnTrip.text == "Start"){
                    btnTrip.text = "Stop"
                    tripListener?.registerSensor()

                }else{
                    btnTrip.text = "Start"
                    tripListener?.removeSensorListeners()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)
        btnTrip.setOnClickListener(this)

        tripListener = SensorAcceleListener(this)
        tripListener?.registerObserver(this)

    }


    override fun onStartListening() {


    }


    override fun onFinishListening() {
        val intentInput = Intent(this, SaveFileService::class.java)
        intentInput.putExtra(Constants.POSITION, "")
        intentInput.putExtra(Constants.CSV_STRING, "")
        intentInput.action = Constants.ACTION_SAVE_FILE_TRIP
        startService(intentInput)
    }

    override fun onDestroy() {
        super.onDestroy()
        SensorAcceleListener.mTripThread.quitSafely()

    }
}
