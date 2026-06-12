package com.atakmap.android.kotlinstarter.util

import android.util.Log

/** Single logging tag so `adb logcat | grep KotlinStarter` (and atak-mcp) just works. */
object L {
    const val TAG = "KotlinStarter"

    fun d(msg: String) = Log.d(TAG, msg)
    fun w(msg: String) = Log.w(TAG, msg)
    fun e(msg: String, t: Throwable? = null) = Log.e(TAG, msg, t)
}
