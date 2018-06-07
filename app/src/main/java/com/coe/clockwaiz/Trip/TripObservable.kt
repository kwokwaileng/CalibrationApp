package com.coe.clockwaiz.Trip



interface TripObservable {
    fun registerObserver(observer: TripObserver)
    fun removeObserver(observer: TripObserver)
    fun notifyObserver(position: String)
}