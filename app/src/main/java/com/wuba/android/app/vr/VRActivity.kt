package com.wuba.android.app.vr

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.asha.vrlib.MDDirectorCamUpdate
import com.asha.vrlib.MDVRLibrary
import com.asha.vrlib.texture.MD360BitmapTexture
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.wuba.android.app.R

class VRActivity : AppCompatActivity() {

    private var isNormal: Boolean = false
    private var mVRLibrary: MDVRLibrary? = null
    private var mGLSurfaceView: GLSurfaceView? = null
    private var mTvModel: TextView? = null
    private var mCallback: MD360BitmapTexture.Callback? = null
    private var sensorOpen: Boolean = false
    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vr_activity)
        initView()
        initData()
    }


    private fun initView() {
        mGLSurfaceView = findViewById(R.id.glSurfaceView)
        mTvModel = findViewById(R.id.tvModel)
        mTvModel?.setOnClickListener {
            if (isNormal) {
                normalMode()
                mTvModel?.text = "鱼眼"
            }else{
                fishEyesMode()
                mTvModel?.text = "普通"
            }
        }
    }

    private fun initData() {
        mVRLibrary = MDVRLibrary.with(this)
            .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
            .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
            .asBitmap { callback ->
                mCallback = callback
                loadImage("file:///android_asset/3.jpg",mCallback)
            }
            .listenTouchPick { hitHotspot, ray -> }
            .pinchEnabled(true)
            .build(mGLSurfaceView)
    }

    private fun loadImage(photos: String, callback: MD360BitmapTexture.Callback?) {
        Glide.with(this)
            .asBitmap()
            .override(4096, 4096/2)
            .load(photos)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?,
                ) {
                    mVRLibrary?.onTextureResize(resource.width.toFloat(), resource.height.toFloat())
                    callback?.texture(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    fun fishEyesMode() {
        val cameraUpdate = mVRLibrary?.updateCamera() ?: return
        isNormal = true
        val near = PropertyValuesHolder.ofFloat("near", cameraUpdate.nearScale, -0.6f)
        val eyeZ = PropertyValuesHolder.ofFloat("eyeZ", cameraUpdate.eyeZ, 18f)
        val pitch = PropertyValuesHolder.ofFloat("pitch", cameraUpdate.pitch, 45f)
        val yaw = PropertyValuesHolder.ofFloat("yaw", cameraUpdate.yaw, 45f)
        val roll = PropertyValuesHolder.ofFloat("roll", cameraUpdate.roll, 0f)
        startCameraAnimation(cameraUpdate, near, eyeZ, pitch, yaw, roll)
    }

    fun normalMode() {
        val cameraUpdate = mVRLibrary?.updateCamera() ?: return
        isNormal = false
        val near = PropertyValuesHolder.ofFloat("near", cameraUpdate.nearScale, 0f)
        val eyeZ = PropertyValuesHolder.ofFloat("eyeZ", cameraUpdate.eyeZ, 0f)
        val pitch = PropertyValuesHolder.ofFloat("pitch", cameraUpdate.pitch, 0f)
        val yaw = PropertyValuesHolder.ofFloat("yaw", cameraUpdate.yaw, 0f)
        val roll = PropertyValuesHolder.ofFloat("roll", cameraUpdate.roll, 0f)
        startCameraAnimation(cameraUpdate, near, eyeZ, pitch, yaw, roll)
    }

    private fun startCameraAnimation(
        cameraUpdate: MDDirectorCamUpdate,
        vararg values: PropertyValuesHolder,
    ) {
        animator?.cancel()
        animator = ValueAnimator.ofPropertyValuesHolder(*values).setDuration(2000)
        animator?.addUpdateListener { animation ->
            val near = animation.getAnimatedValue("near") as Float
            val eyeZ = animation.getAnimatedValue("eyeZ") as Float
            val pitch = animation.getAnimatedValue("pitch") as Float
            val yaw = animation.getAnimatedValue("yaw") as Float
            val roll = animation.getAnimatedValue("roll") as Float
            cameraUpdate.setEyeZ(eyeZ).setNearScale(near).setPitch(pitch).setYaw(yaw).roll = roll
        }
        animator?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVRLibrary?.onDestroy()
    }
}