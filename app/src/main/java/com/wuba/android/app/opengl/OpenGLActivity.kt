package com.wuba.android.app.opengl

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.wuba.android.app.R

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
        Glide.with(this)
            .asBitmap()
            .override(4096, 4096/2)
            .load("file:///android_asset/2.jpg")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?,
                ) {
                    mGLSurfaceRenderView?.setBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }
}
