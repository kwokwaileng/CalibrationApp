package com.coe.clockwaiz

import android.content.pm.PackageManager
import com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo
import android.R.attr.versionName
import android.content.Context
import android.text.TextUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.support.annotation.NonNull
import android.util.Log


class ForceUpdateChecker(private val context: Context,
                         private val onUpdateNeededListener: OnUpdateNeededListener?) {

    interface OnUpdateNeededListener {
        fun onUpdateNeeded()
    }

    fun check() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        Log.d("UPDATE"," KEY_UPDATE_REQUIRED ${remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)}")
        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            val currentVersion = remoteConfig.getString(VERSION_APP)
            val appVersion = getAppVersion(context)


            Log.d("UPDATE"," COMPARE Current version $currentVersion || app version $appVersion")
            if (!TextUtils.equals(currentVersion, appVersion) && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded()
            }
        }
    }

    private fun getAppVersion(context: Context): String {
        var result = ""

        try {
            result = context.packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message)
        }

        return result
    }

    class Builder(private val context: Context) {
        private var onUpdateNeededListener: OnUpdateNeededListener? = null

        fun onUpdateNeeded(onUpdateNeededListener: OnUpdateNeededListener): Builder {
            this.onUpdateNeededListener = onUpdateNeededListener
            return this
        }

        fun build(): ForceUpdateChecker {
            return ForceUpdateChecker(context, onUpdateNeededListener)
        }

        fun check(): ForceUpdateChecker {
            val forceUpdateChecker = build()
            forceUpdateChecker.check()

            return forceUpdateChecker
        }
    }

    companion object {

        private val TAG = ForceUpdateChecker::class.java.simpleName

        val VERSION_APP = "version_app"
        val KEY_UPDATE_REQUIRED = "update_required"


        fun with(context: Context): Builder {
            return Builder(context)
        }
    }
}