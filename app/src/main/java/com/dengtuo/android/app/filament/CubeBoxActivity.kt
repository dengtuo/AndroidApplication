package com.dengtuo.android.app.filament

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.dengtuo.android.app.R
import com.dengtuo.android.app.utis.BitmapAbility
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Filament
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.Skybox
import com.google.android.filament.Texture
import com.google.android.filament.Texture.PixelBufferDescriptor
import com.google.android.filament.View
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class CubeBoxActivity : AppCompatActivity() {

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
        loadTexture(engine)
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

    private fun loadTexture(engine: Engine) {

        val size = IntArray(2)
        peekSize(assets, "image/right.jpg", size)

        val texture = Texture.Builder()
            .width(size.get(0))
            .height(size.get(1))
            .levels(0xff)
            .format(Texture.InternalFormat.R11F_G11F_B10F)
            .sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .build(engine)
        val level = texture.levels
        val faceSize = texture.getWidth(0) * texture.getHeight(0) * 4
        val storage = ByteBuffer.allocateDirect(faceSize * 6)
        loadCubeBitmap().forEach {
            it.copyPixelsToBuffer(storage)
        }
        val buffer = PixelBufferDescriptor(
            storage, Texture.Format.RGB,
            Texture.Type.UINT_10F_11F_11F_REV
        )
        storage.flip()
        texture.setImage(
            engine,
            texture.levels,
            0,
            0,
            0,
            texture.getWidth(level),
            texture.getHeight(level),
            1,
            buffer
        )
    }

    private fun loadCubeBitmap(): ArrayList<Bitmap> {
        val bitmaps = ArrayList<Bitmap>()

        var bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/right.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }
        bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/left.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }

        bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/top.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }

        bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/bottom.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }

        bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/front.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }

        bitmap = BitmapAbility.decodeAssetsBitmap(
            this@CubeBoxActivity, "image/back.jpg", Int.MAX_VALUE,
            Int.MAX_VALUE
        )
        bitmap?.let { bitmaps.add(it) }
        return bitmaps
    }

    private fun peekSize(assets: AssetManager, name: String, size: IntArray) {
        var input: InputStream? = null
        try {
            input = assets.open(name)
        } catch (e: IOException) {
            Log.e("Filament", "Unable to peek at cubemap: $name")
            e.printStackTrace()
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, options)
        size[0] = options.outWidth
        size[1] = options.outHeight
    }
}