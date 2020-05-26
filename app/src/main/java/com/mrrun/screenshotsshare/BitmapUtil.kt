package com.mrrun.screenshotsshare

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import java.io.File

object BitmapUtil {

    fun concatBitmap(context: Context, filePath: String?, secondBitmap: Bitmap?): Bitmap? {
        if (secondBitmap == null) {
            return null
        }
        val navHeight: Int =
            ScreenUtil.getHeightWithNav(context) - ScreenUtil.getHeightWithoutNav(context)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inPreferredConfig = Bitmap.Config.RGB_565
        BitmapFactory.decodeFile(filePath, options)
        val width = options.outWidth
        val height = options.outHeight - navHeight
        val max = 1024 * 1024
        var sampleSize = 1
        while (width / sampleSize * height / sampleSize > max) {
            sampleSize *= 2
        }
        options.inSampleSize = sampleSize
        options.inJustDecodeBounds = false
        val srcBmp = BitmapFactory.decodeFile(filePath, options)
        //先计算bitmap的宽高，因为bitmap的宽度和屏幕宽度是不一样的，需要按比例拉伸
        val ratio = 1.0 * secondBitmap.width / srcBmp.width
        val additionalHeight = (secondBitmap.height / ratio).toInt()
        val scaledBmp =
            Bitmap.createScaledBitmap(secondBitmap, srcBmp.width, additionalHeight, false)
        //到这里图片拉伸完毕
        //这里开始拼接，画到Canvas上
        val result = Bitmap.createBitmap(
            srcBmp.width,
            srcBmp.height - navHeight / sampleSize + additionalHeight,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas()
        canvas.setBitmap(result)
        canvas.drawBitmap(srcBmp, 0F, 0F, null)
        //这里需要做个判断，因为一些系统是有导航栏的，所以截图时有导航栏，这里需要把导航栏遮住
        //计算出导航栏高度，然后draw时往上偏移一段距离
        val navRatio: Double =
            1.0 * ScreenUtil.getDisplayWidth(context as Activity)!! / srcBmp.width
        canvas.drawBitmap(
            scaledBmp,
            0F,
            (srcBmp.height - (navHeight / navRatio).toInt()).toFloat(),
            null
        )
        secondBitmap.recycle()
        return result
    }

    fun file2Bitmap(filePath: String?): Bitmap? {
        val file = File(filePath)
        return if (file.exists()) {
            BitmapFactory.decodeFile(filePath)
        } else {
            Log.e("", "The file is not exist!")
            null
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}