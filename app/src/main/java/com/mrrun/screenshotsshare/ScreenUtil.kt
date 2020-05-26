package com.mrrun.screenshotsshare

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object ScreenUtil {

    /**
     * 获取屏幕高度，不包括navigation
     */
    fun getHeightWithoutNav(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = windowManager.defaultDisplay
        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        } else {
            try {
                val method: Method = d.javaClass.getDeclaredMethod("getRealMetrics")
                method.isAccessible = true
                method.invoke(d, realDisplayMetrics)
            } catch (e: NoSuchMethodException) {
            } catch (e: InvocationTargetException) {
            } catch (e: IllegalAccessException) {
            }
        }
        return realDisplayMetrics.heightPixels
    }

    /**
     * 获取屏幕高度，包括navigation
     *
     * @return
     */
    fun getHeightWithNav(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 获取屏幕分辨率
     */
    fun defaultDisplay(activity: Activity): IntArray? {
        val pixels = IntArray(2)
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        pixels[0] = dm.widthPixels
        pixels[1] = dm.heightPixels
        return pixels
    }

    /**
     * 获取屏幕宽度
     */
    fun getDisplayWidth(activity: Activity): Int? {
        return defaultDisplay(activity)?.get(0)
    }
}