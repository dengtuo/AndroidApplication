package com.dengtuo.android.app.filament

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceView
import com.dengtuo.android.app.R
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Filament
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.Skybox
import com.google.android.filament.View
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper

class PanoramaFilamentActivity : AppCompatActivity() {

    companion object {
        init {
            Filament.init()
        }
    }


    private var surfaceView: SurfaceView? = null
    private var displayHelper: DisplayHelper? = null

    //filament
    private val engine: Engine by lazy {
        Engine.create()
    }
    private var view: View? = null
    private var scene: Scene? = null
    private var camera: Camera? = null
    private var renderer: Renderer? = null

    //help
    private var choreographer: Choreographer? = null
    private var uiHelper: UiHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panorama_filament)
        initView()
        initData()
    }

    private fun initView() {
        surfaceView = findViewById(R.id.view_surfaceView)
    }

    private fun initData() {
        choreographer = Choreographer.getInstance()
        displayHelper = DisplayHelper(this)
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
        uiHelper?.renderCallback = renderCallback
        surfaceView?.let { uiHelper?.attachTo(it) }
        setupFilament()
        setupView()
        setupScene()
    }


    private fun setupFilament() {
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera(engine.entityManager.create())
    }

    private fun setupView() {
        scene?.skybox = Skybox.Builder()
            .color(0.035f, 0.035f, 0.035f, 1.0f)
            .build(engine)
        view?.camera = camera
        view?.scene = scene
    }

    private fun setupScene() {

    }

    private val frameScheduler = Choreographer.FrameCallback {

    }

    private val renderCallback = object : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface?) {

        }

        override fun onDetachedFromSurface() {

        }

        override fun onResized(width: Int, height: Int) {

        }

    }
}