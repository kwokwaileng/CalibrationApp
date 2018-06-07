package com.coe.clockwaiz.sensors

/**
 *
 * Created by nithin on 27/10/17.
 */
interface SensorObserver {


    fun onStartListening()
    fun onFinishListening(csvEvent:String , csvSummary:String)
    fun onFinishData(xAverage:Double, yAverage:Double, zAverage:Double, position:String)



}