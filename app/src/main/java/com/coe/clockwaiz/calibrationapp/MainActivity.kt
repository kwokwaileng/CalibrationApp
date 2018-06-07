package com.coe.clockwaiz.calibrationapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.coe.clockwaiz.Constants
import com.coe.clockwaiz.PositionOffsetPref
import com.coe.clockwaiz.calibrationapp.R.id.txtTitle
import kotlinx.android.synthetic.main.activity_calibrate_front.*
import kotlinx.android.synthetic.main.activity_main.*
import android.R.menu
import android.view.MenuInflater
import android.view.MenuItem
import com.coe.clockwaiz.ForceUpdateChecker
import com.coe.clockwaiz.Trip.TripActivity
import com.coe.clockwaiz.database.CalibrationDatabase
import android.content.DialogInterface




class MainActivity : AppCompatActivity(), View.OnClickListener,ForceUpdateChecker.OnUpdateNeededListener {


    override fun onUpdateNeeded() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update"
                ) { _, _ ->
                    run {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.setClassName("io.crash.air", "io.crash.air.ui.MainActivity")
                        startActivity(intent)
                    }
                }.setNegativeButton("No, thanks"
                ) { _, _ -> finish() }.create()
        dialog.show()
    }

    var positionOffsetPref: PositionOffsetPref = PositionOffsetPref()

    var xOffSet: Float = 0.0f
    var yOffSet: Float = 0.0f
    var zOffSet: Float = 0.0f
    lateinit var  tv_title: TextView

    private fun initialiseButton() {

        btnCalibrateFront.setOnClickListener(this)
        btnCalibrateBack.setOnClickListener(this)
        btnCalibratePortraitUp.setOnClickListener(this)
        btnCalibratePortraitDown.setOnClickListener(this)
        btnCalibrateLandscapeLeft.setOnClickListener(this)
        btnCalibrateLandscapeRight.setOnClickListener(this)
        tv_title.setOnClickListener(this)


    }

    private fun displaySummary(){

        xOffSet = (positionOffsetPref.xOffsetFront+positionOffsetPref.xOffsetBack+positionOffsetPref.xOffsetPortraitUp+positionOffsetPref.xOffsetPortraitDown)/4
        yOffSet = (positionOffsetPref.yOffsetFront+positionOffsetPref.yOffsetBack+positionOffsetPref.yOffSetLandscapeLeft+positionOffsetPref.yOffsetLandscapeRight)/4
        zOffSet = (positionOffsetPref.zOffsetPortraitUp+positionOffsetPref.zOffsetPortraitDown+positionOffsetPref.zOffSetLandscapeLeft+positionOffsetPref.zOffsetLandscapeRight)/4


        positionOffsetPref.xOffset =xOffSet
        positionOffsetPref.yOffset =yOffSet
        positionOffsetPref.zOffset =zOffSet

        val summary = "---SUMMARY---\n" +
                "X Ref: ${positionOffsetPref.xRefValueLandscapeLeft}\n" +
                "Y Ref ${positionOffsetPref.yRefValuePortraitUp}\n" +
                "Z Ref ${positionOffsetPref.zRefValueFront}\n" +
                "\n" +
                "X Offset $xOffSet\n" +
                "Y Offset $yOffSet\n" +
                "Z Offset $zOffSet\n"

        val alert = AlertDialog.Builder(this)

        alert.setTitle("Summary ")
        alert.setMessage(summary)
        alert.setCancelable(false)
        alert.setPositiveButton("Ok"){
            dialog, _ ->
            dialog.dismiss()


        }
        alert.show()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnCalibrateFront -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_FRONT)
                startActivity(homeIntent)
            }

            R.id.btnCalibrateBack -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_BACK)
                startActivity(homeIntent)
            }

            R.id.btnCalibratePortraitUp -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_PORTRAIT_UP)
                startActivity(homeIntent)
            }

            R.id.btnCalibratePortraitDown -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_PORTRAIT_DOWN)
                startActivity(homeIntent)
            }

            R.id.btnCalibrateLandscapeLeft -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_LANDSCAPE_LEFT)
                startActivity(homeIntent)
            }

            R.id.btnCalibrateLandscapeRight -> {
                val homeIntent = Intent(this, CalibrateActivity::class.java)
                homeIntent.putExtra("POSITION", Constants.POSITION_LANDSCAPE_RIGHT)
                startActivity(homeIntent)
            }
            R.id.txtTitle -> {
                displaySummary()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_calibrate)

        ForceUpdateChecker.with(this).onUpdateNeeded(this).check()
        tv_title = findViewById(R.id.txtTitle)
        requestPermissionsForFileWrite()
        initialiseButton()
    }

    private fun requestPermissionsForFileWrite() {
        if (this.let { this.let { it1 -> ActivityCompat.checkSelfPermission(it1, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } } != PackageManager.PERMISSION_GRANTED) {
            this.let {
                ActivityCompat.requestPermissions(it,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1001)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calibration_menu, menu)
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.action_trip -> {
                val tripIntent = Intent(this, TripActivity::class.java)
                startActivity(tripIntent)
            }

        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        CalibrationDatabase.destroyInstance()
    }
}
