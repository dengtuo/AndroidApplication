package com.dengtuo.android.app.opengl

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.dengtuo.android.app.R
import com.dengtuo.android.app.utis.BitmapAbility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class OpenGLActivity : AppCompatActivity() {

    private var mGLSurfaceRenderView: GLSurfaceRenderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_layout)
        initView()
        initData()
    }


    private fun initView() {
        mGLSurfaceRenderView = findViewById(R.id.gl_render_view)
    }

    private fun initData() {
       loadCubeBitmap()
    }

    private fun loadSkySphere(){
        runBlocking(Dispatchers.IO){
            val bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/2.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { mGLSurfaceRenderView?.setBitmap(it) }
        }
    }

    private fun loadCubeBitmap(){
        runBlocking(Dispatchers.IO){
            val bitmaps = ArrayList<Bitmap>()

            var bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/left.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }
            bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/right.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/bottom.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/top.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/front.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }

            bitmap = BitmapAbility.decodeAssetsBitmap(this@OpenGLActivity,"image/back.jpg", Int.MAX_VALUE,
                Int.MAX_VALUE)
            bitmap?.let { bitmaps.add(it) }
            mGLSurfaceRenderView?.setCubeBitmap(bitmaps)
        }
    }
}
