package com.dengtuo.android.app.opengl

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dengtuo.android.app.R
import com.dengtuo.android.app.utis.BitmapAbility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class OpenGLActivity : AppCompatActivity() {

    private var mGLSurfaceRenderView: GLSurfaceRenderView? = null
    private var type = 0

    companion object {
        const val RENDERER_TYPE_KEY = "renderer_key"
        const val CUBE_TYPE = 1
        const val SPHERE_TYPE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_layout)
        type = intent.getIntExtra(RENDERER_TYPE_KEY, 0)
        initView()
        initData()
    }


    private fun initView() {
        mGLSurfaceRenderView = findViewById(R.id.gl_render_view)
    }

    private fun initData() {
        mGLSurfaceRenderView?.setRendererType(type)
        when (type) {
            CUBE_TYPE -> {
                loadCubeBitmap()
            }

            SPHERE_TYPE -> {
                loadSkySphere()
            }
        }
    }

    private fun loadSkySphere() {
        runBlocking(Dispatchers.IO) {
            val bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/独立楼梯_其他2.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { mGLSurfaceRenderView?.setBitmap(it) }
        }
    }

    private fun loadCubeBitmap() {
        runBlocking(Dispatchers.IO) {
            val bitmaps = ArrayList<Bitmap>()

            var bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/left.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }
            bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/right.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/bottom.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/top.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/front.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(
                this@OpenGLActivity, "image/back.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE
            )
            bitmap?.let { bitmaps.add(it) }
            mGLSurfaceRenderView?.setCubeBitmap(bitmaps)
        }
    }
}
