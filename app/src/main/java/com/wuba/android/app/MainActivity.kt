package com.wuba.android.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.wuba.android.app.opengl.OpenGLActivity
import com.wuba.android.app.utis.ToastAbility

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.tv_opengl)?.setOnClickListener(this)
    }

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ToastAbility.showToast("Demo#onQRProcessUpdate:status$it")
        }

    private fun doScanQR() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_opengl->{
                val intent = Intent(this, OpenGLActivity::class.java)
                startActivity(intent)
            }
        }
    }
}