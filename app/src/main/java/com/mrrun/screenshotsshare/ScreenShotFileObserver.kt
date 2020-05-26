package com.mrrun.screenshotsshare

import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.TimeUnit

class ScreenShotFileObserver(path: String) : FileObserver(path, ALL_EVENTS) {

    interface ScreenShotLister {
        fun finshScreenShot(path: String?)
        fun beganScreenShot(path: String?)
    }

    private val TAG = "ScreenShotFileObserver"
    /**
     * 监听当次开始截图事件
     */
    private var beganScreenShoted = false
    private var screenShotFileName: String? = ""
    private var screenShotDir: String = path
    private var screenShotLister: ScreenShotLister? = null

    /**
     * 由于某些魅族手机保存有延迟且某些魅族系统上只监听到了CREATE事件，短暂延迟后主动数据处理
     */
    private val handler = Handler(Looper.getMainLooper())
    private var run = Runnable {
        Log.d(TAG, "由于某些魅族手机保存有延迟且某些魅族系统上只监听到了CREATE事件，短暂延迟后主动数据处理")
        dealData()
    }

    override fun onEvent(i: Int, s: String?) {
        when (i) {
            CREATE -> {
                Log.d(TAG, "CREATE = $i , s = $s")
                screenShotFileName = s
                beganScreenShoted = true
                handler.removeCallbacks(run)
                handler.postDelayed(run, TimeUnit.SECONDS.toMillis(1))
                screenShotLister?.beganScreenShot(screenShotDir.plus(screenShotFileName))
            }
            CLOSE_WRITE -> {
                Log.d(TAG, "CLOSE_WRITE = $i , s = $s")
                screenShotFileName = s
                handler.removeCallbacks(run)
                dealData()
            }
        }
    }

    /**
     * 数据处理
     */
    private fun dealData() {
        beganScreenShoted = false
        Log.d(TAG, "screenShotFileName = $screenShotFileName")
        screenShotLister?.finshScreenShot(screenShotDir.plus(screenShotFileName))
    }

    fun setLister(lister: ScreenShotLister) {
        screenShotLister = lister
    }

    fun removeLister() {
        screenShotLister = null
    }
}