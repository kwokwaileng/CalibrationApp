package com.coe.clockwaiz

import android.app.Application
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.android.gms.tasks.OnCompleteListener



class CalibrationApp : Application() {


    override fun onCreate() {
        super.onCreate()
        Preferences.init(applicationContext)

        val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        var remoteConfigDefaults = HashMap<String, Any>()


        remoteConfigDefaults[Constants.VERSION_APP] = "0.0"
        remoteConfigDefaults[Constants.UPDATE_REQUIRED] = false

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults)
        firebaseRemoteConfig.fetch(60).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                    }
                }

        }


}


