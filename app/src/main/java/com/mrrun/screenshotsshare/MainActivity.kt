package com.mrrun.screenshotsshare

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val RC_READ_WRITE_EXTERNAL_STORAGE = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsA()
    }

    private fun requestPermissionsA() {
        val perms =
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        //判断有没有权限
        if (EasyPermissions.hasPermissions(this, *perms)) { // 如果有权限了, 就做你该做的事情
            /*val PATH: String =
                Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Screenshots"
            ScreenShotFileObserver(PATH).startWatching()*/
        } else { // 如果没有权限, 就去申请权限
            // this: 上下文
            // Dialog显示的正文
            // RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
            // perms 就是你要申请的权限
            EasyPermissions.requestPermissions(
                this,
                "写上你需要用权限的理由, 是给用户看的",
                RC_READ_WRITE_EXTERNAL_STORAGE,
                *perms
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        ScreenShotFileObserverManager.registerScreenShotFileObserver(object :
            ScreenShotFileObserver.ScreenShotLister {
            override fun finshScreenShot(path: String?) {
                var secondBitmap =
                    BitmapUtil.drawableToBitmap(this@MainActivity.getDrawable(R.mipmap.ic_launcher)!!)
                Log.d(TAG, "finshScreenShot path = $path")
                Log.d(TAG, "secondBitmap = $secondBitmap")
                var bitmap = BitmapUtil.concatBitmap(
                    this@MainActivity,
                    path,
                    secondBitmap
                )
                Log.d(TAG, "bitmap = $bitmap")
                runOnUiThread { image.setImageBitmap(bitmap) }
            }

            override fun beganScreenShot(path: String?) {
                Log.d(TAG, "beganScreenShot path = $path")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        ScreenShotFileObserverManager.unregisteScreenShotFileObserver()
    }
}