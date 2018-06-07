package com.coe.clockwaiz.sensors

/**
 *
 * Created by nithin on 27/10/17.
 */
interface SensorObservable {

    fun registerObserver(observer: SensorObserver)
    fun removeObserver(observer: SensorObserver)
    fun notifyObserver(position: String )
}