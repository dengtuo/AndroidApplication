package com.wuba.android.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.client.android.CaptureActivity
import com.google.zxing.client.android.Intents
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
        val intent = Intent(this, CaptureActivity::class.java)
        intent.action = Intents.Scan.ACTION
        intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 80L)
        //扫码类型
        //扫码类型
        val formats = BarcodeFormat.QR_CODE.toString()
        intent.putExtra(Intents.Scan.FORMATS, formats)
        //扫码hint
        val bundle = Bundle()
        bundle.putString(DecodeHintType.CHARACTER_SET.name, "utf-8")
        bundle.putBoolean(DecodeHintType.TRY_HARDER.name, java.lang.Boolean.TRUE)
        bundle.putString(DecodeHintType.POSSIBLE_FORMATS.name, BarcodeFormat.QR_CODE.toString())
        intent.putExtras(bundle)
        scanLauncher.launch(intent)
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