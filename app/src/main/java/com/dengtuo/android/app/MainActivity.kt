package com.dengtuo.android.app

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dengtuo.android.app.filament.CubeBoxActivity
import com.dengtuo.android.app.opengl.OpenGLActivity
import com.dengtuo.android.app.utis.ToastAbility
import com.dengtuo.android.app.utis.log.LogAbility

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.tv_opengl_sky_sphere)?.setOnClickListener(this)
        findViewById<View>(R.id.tv_opengl_cube_box)?.setOnClickListener(this)
        findViewById<View>(R.id.tv_filament)?.setOnClickListener(this)
        findViewById<View>(R.id.tv_scroll)?.setOnClickListener(this)
        findViewById<View>(R.id.tv_soter)?.setOnClickListener(this)
        findViewById<View>(R.id.tv_coroutine)?.setOnClickListener(this)
    }

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ToastAbility.showToast("Demo#onQRProcessUpdate:status$it")
        }

    private fun doScanQR() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_opengl_sky_sphere -> {
                val intent = Intent(this, OpenGLActivity::class.java)
                intent.putExtra(OpenGLActivity.RENDERER_TYPE_KEY,OpenGLActivity.SPHERE_TYPE)
                startActivity(intent)
            }

            R.id.tv_opengl_cube_box -> {
                val intent = Intent(this, OpenGLActivity::class.java)
                intent.putExtra(OpenGLActivity.RENDERER_TYPE_KEY,OpenGLActivity.CUBE_TYPE)
                startActivity(intent)
            }

            R.id.tv_filament -> {
                val intent = Intent(this, CubeBoxActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_scroll -> {
                val intent = Intent(this, GridActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_soter -> {
                bindSoterService()
            }
            R.id.tv_coroutine -> {
                bindSoterService()
            }
        }
    }

    private fun bindSoterService() {
        val intent = Intent()
        intent.action = "com.tencent.soter.soterserver.ISoterService"
        intent.setPackage("com.tencent.soter.soterserver")
        bindService(intent, mSoterServiceConnection, BIND_AUTO_CREATE)
    }


    private val mSoterServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogAbility.d(TAG,"onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LogAbility.d(TAG,"onServiceDisconnected")
        }

    }

    companion object{
        private const val TAG = "MainActivity"
    }
}