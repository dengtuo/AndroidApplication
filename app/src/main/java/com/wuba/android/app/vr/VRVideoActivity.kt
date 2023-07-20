package com.wuba.android.app.vr

import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.asha.vrlib.MDVRLibrary
import com.wuba.android.app.R
import java.io.IOException


class VRVideoActivity : Activity(), View.OnClickListener {

    private val TAG: String = "VRVideoActivity"
    private var mMediaPlayer: MediaPlayer? = null
    private var isNormal: Boolean = false
    private var mVRLibrary: MDVRLibrary? = null
    private var mTvModel: TextView? = null
    private var mCallback: MDVRLibrary.IOnSurfaceReadyCallback? = null
    private var sensorOpen: Boolean = false
    private var animator: ValueAnimator? = null

    private var mGLSurfaceView: GLSurfaceView? = null

    private fun initData() {
        mVRLibrary = MDVRLibrary.with(this)
            .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
            .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
            .asVideo {
                mMediaPlayer?.setSurface(it)
            }
            .listenTouchPick { hitHotspot, ray -> }
            .pinchEnabled(true)
            .build(mGLSurfaceView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vr_video)
        mGLSurfaceView = findViewById(R.id.surface_view)
        val holder = mGLSurfaceView?.holder
        holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // createMediaPlayer方法必须要等待Surface被创建以后调用
                createMediaPlayer()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        findViewById<Button>(R.id.btn_create).setOnClickListener(this)
        findViewById<Button>(R.id.btn_start).setOnClickListener(this)
        findViewById<Button>(R.id.btn_pause).setOnClickListener(this)
        findViewById<Button>(R.id.btn_stop).setOnClickListener(this)
        mMediaPlayer = MediaPlayer()
//        mMediaPlayer = MediaPlayer.create(this, com.asha.vrlib.R.raw.per_pixel_vertex_shader)
        val listener = MediaPlayerListener()
        mMediaPlayer?.setOnPreparedListener(listener)
        mMediaPlayer?.setOnCompletionListener(listener)
        // initView()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mVRLibrary?.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_create -> {
                createMediaPlayer()
                startMediaPlayer()
            }
            R.id.btn_start -> {
                startMediaPlayer()
            }
            R.id.btn_pause -> {
                pauseMediaPlayer()
            }
            R.id.btn_stop -> {
                stopMediaPlayer()
            }
        }
    }


    private fun createMediaPlayer() {
        try {
            mMediaPlayer?.reset()
//            val fileDescriptor = assets.openFd("4.mp4")
//            mMediaPlayer?.setDataSource(
//                fileDescriptor.fileDescriptor,
//                fileDescriptor.startOffset,
//                fileDescriptor.length
//            )
//            mMediaPlayer?.setDataSource(this, Uri.parse("https://wos2.58cdn.com.cn/LmNiHgFeLmTs/overseapano/8f009b54222dc9a6ca01e5b26a33815eoutput.mp4"))
            mMediaPlayer?.setDataSource(this, Uri.parse("https://c.58cdn.com.cn/git/teg-app-fe/vr-saas-product-viewer/static/media/30timevr.mp4"))
            mMediaPlayer?.prepareAsync()

        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun startMediaPlayer() {
        try {
            mMediaPlayer?.start()
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun pauseMediaPlayer() {
        try {
            mMediaPlayer?.pause()
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun stopMediaPlayer() {
        try {
            mMediaPlayer?.stop()
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.toString())
        }
    }

    private inner class MediaPlayerListener : OnPreparedListener, OnCompletionListener {

        override fun onPrepared(mp: MediaPlayer) {
            Toast.makeText(this@VRVideoActivity, "onPrepared", Toast.LENGTH_SHORT).show()
            mMediaPlayer?.start()
        }

        override fun onCompletion(mp: MediaPlayer) {
            Toast.makeText(this@VRVideoActivity, "onCompletion", Toast.LENGTH_SHORT).show()
        }

    }

}