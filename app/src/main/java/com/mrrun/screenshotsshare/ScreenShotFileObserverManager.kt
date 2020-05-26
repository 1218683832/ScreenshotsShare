package com.mrrun.screenshotsshare

import android.os.Environment
import android.util.Log
import java.io.File

object ScreenShotFileObserverManager {

    private var screenShotFileObserver: ScreenShotFileObserver? = null

    /**
     * 截屏依据中的截屏文件夹判断关键字
     */
    private val KEYWORDS = listOf(
        "screenshots", "screenshot", "screen_shot", "screen-shot", "screen shot",
        "screencapture", "screen_capture", "screen-capture", "screen capture",
        "screencap", "screen_cap", "screen-cap", "screen cap", "截屏"
    )
    /**
     * 截屏文件夹根路径
     */
    private val SCREENSHOT_ROOT_PATH: String =
        Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DCIM + File.separator
    private const val TAG = "ObserverManager"
    /**
     * 截屏文件夹路径
     */
    private var screenshot_path = ""

    /**
     * 注册内容监听
     */
    fun registerScreenShotFileObserver(lister: ScreenShotFileObserver.ScreenShotLister) {
        Log.d(TAG, "registerScreenShotFileObserver")
        Log.d(TAG, "screenshot_root_path = $SCREENSHOT_ROOT_PATH")
        Log.d(TAG, "screenshot_path = $screenshot_path")
        screenShotFileObserver = screenShotFileObserver ?: let {
            for (keyword in KEYWORDS) {
                val s = SCREENSHOT_ROOT_PATH.plus(keyword)
                Log.d(TAG, "搜查 keyword = $keyword")
                Log.d(TAG, "s = $s")
                if (File(s).exists()) {
                    screenshot_path = s.plus(File.separator)// 找到了截图文件夹
                    Log.d(TAG, "找到了截图文件夹 path = $screenshot_path")
                    break
                }
            }
            ScreenShotFileObserver(screenshot_path)
        }
        screenShotFileObserver?.setLister(lister)
        screenShotFileObserver?.startWatching()
    }

    /**
     * 注销内容监听
     */
    fun unregisteScreenShotFileObserver() {
        Log.d(TAG, "unregisteScreenShotFileObserver")
        screenShotFileObserver?.removeLister()
        screenShotFileObserver?.stopWatching()
    }
}