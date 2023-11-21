package com.dengtuo.android.app.filament

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.Surface
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.dengtuo.android.app.R
import com.dengtuo.android.app.utis.BitmapAbility
import com.dengtuo.android.app.utis.log.LogAbility
import com.google.android.filament.Box
import com.google.android.filament.Camera
import com.google.android.filament.Colors
import com.google.android.filament.Engine
import com.google.android.filament.Entity
import com.google.android.filament.EntityManager
import com.google.android.filament.Filament
import com.google.android.filament.IndexBuffer
import com.google.android.filament.Material
import com.google.android.filament.MaterialInstance
import com.google.android.filament.RenderableManager
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.Skybox
import com.google.android.filament.SwapChain
import com.google.android.filament.Texture
import com.google.android.filament.Texture.PixelBufferDescriptor
import com.google.android.filament.TextureSampler
import com.google.android.filament.VertexBuffer
import com.google.android.filament.View
import com.google.android.filament.Viewport
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.FilamentHelper
import com.google.android.filament.android.UiHelper
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CubeBoxActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "CubeBoxActivity"

        init {
            Filament.init()
        }
    }


    private var mSurfaceView: SurfaceView? = null
    private var mDisplayHelper: DisplayHelper? = null

    @Entity
    private var renderable = 0

    //filament
    private val mEngine: Engine by lazy {
        Engine.create()
    }
    private var mViewportView: View? = null
    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderer: Renderer? = null

    private var mMaterial: Material? = null

    //help
    private var mChoreographer: Choreographer? = null
    private var mUiHelper: UiHelper? = null
    private var mMaterialInstance: MaterialInstance? = null
    private var mSkybox: Skybox? = null

    private var mVertexBuffer: VertexBuffer? = null
    private var mIndexBuffer: IndexBuffer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panorama_filament)
        initView()
        initData()
    }


    override fun onResume() {
        super.onResume()
        mChoreographer?.postFrameCallback(frameScheduler)
    }

    override fun onPause() {
        super.onPause()
        mChoreographer?.removeFrameCallback(frameScheduler)
    }

    private fun initView() {
        mSurfaceView = findViewById(R.id.view_surfaceView)
    }

    private fun initData() {
        mChoreographer = Choreographer.getInstance()
        mDisplayHelper = DisplayHelper(this)

        setupSurfaceView()
        setupFilament()
        setupScene()

    }

    private fun setupSurfaceView() {
        val surfaceView = mSurfaceView ?: return
        mUiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
        mUiHelper?.renderCallback = renderCallback

        // NOTE: To choose a specific rendering resolution, add the following line:
        // uiHelper.setDesiredSize(1280, 720)

        mUiHelper?.attachTo(surfaceView)
    }


    private fun setupFilament() {
        mRenderer = mEngine.createRenderer()
        mScene = mEngine.createScene()
        mViewportView = mEngine.createView()
        mCamera = mEngine.createCamera(mEngine.entityManager.create())
    }


    private fun setupScene() {
        val texture = loadTexture(mEngine)
        loadMaterial()
        //createMesh()
        setupMaterial(texture)
        val vertexBuffer = mVertexBuffer?:return
        val indexBuffer = mIndexBuffer?:return


        mSkybox = Skybox.Builder().environment(texture).build(mEngine)
        mScene?.skybox = mSkybox

        mViewportView?.camera = mCamera
        mViewportView?.scene = mScene

        renderable = EntityManager.get().create()
        val material = mMaterial ?: return
        // We then create a renderable component on that entity
        // A renderable is made of several primitives; in this case we declare only 1
        RenderableManager.Builder(1)
            // Overall bounding box of the renderable
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.01f))
            // Sets the mesh data of the first primitive
           // .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer, 0, 3)
            // Sets the material of the first primitive
            .material(0, material.defaultInstance)
            .build(mEngine, renderable)

        // Add the entity to the scene to render it
        mScene?.addEntity(renderable)

    }

    private fun setupMaterial(texture:Texture) {
        // Create an instance of the material to set different parameters on it
        mMaterialInstance = mMaterial?.createInstance()
        val sampler = TextureSampler()
        sampler.anisotropy = 8.0f

        // Specify that our color is in sRGB so the conversion to linear
        // is done automatically for us. If you already have a linear color
        // you can pass it directly, or use Colors.RgbType.LINEAR
        mMaterialInstance?.setParameter("skybox",texture,sampler)
    }

    private var swapChain: SwapChain? = null

    private val frameScheduler = object : FrameCallback {

        override fun doFrame(frameTimeNanos: Long) {
            // Schedule the next frame
            mChoreographer?.postFrameCallback(this)
            if (mUiHelper?.isReadyToRender == true) {
                // If beginFrame() returns false you should skip the frame
                // This means you are sending frames too quickly to the GPU
                val v = mViewportView ?: return
                val swap = swapChain ?: return
                if (mRenderer?.beginFrame(swap, frameTimeNanos) == true) {
                    mRenderer?.render(v)
                    mRenderer?.endFrame()
                }
            }
        }

    }

    private val renderCallback = object : UiHelper.RendererCallback {
        override fun onNativeWindowChanged(surface: Surface?) {
            surface ?: return
            val renderer = mRenderer ?: return
            val surfaceView = mSurfaceView ?: return
            swapChain?.let { mEngine.destroySwapChain(it) }
            swapChain = mEngine.createSwapChain(surface)
            mDisplayHelper?.attach(renderer, surfaceView.display)
        }

        override fun onDetachedFromSurface() {
            mDisplayHelper?.detach()
            swapChain?.let {
                mEngine.destroySwapChain(it)
                // Required to ensure we don't return before Filament is done executing the
                // destroySwapChain command, otherwise Android might destroy the Surface
                // too early
                mEngine.flushAndWait()
                swapChain = null
            }
        }

        override fun onResized(width: Int, height: Int) {
            val aspect = width.toDouble() / height.toDouble()
            mCamera?.setProjection(45.0, aspect, 0.1, 20.0, Camera.Fov.VERTICAL)

            mViewportView?.viewport = Viewport(0, 0, width, height)

            FilamentHelper.synchronizePendingFrames(mEngine)
        }

    }

    private fun loadTexture(engine: Engine): Texture {

        val size = IntArray(2)
        peekSize(assets, "image/right.jpg", size)
        val texture = Texture.Builder()
            .width(size.get(0))
            .height(size.get(1))
            .levels(1)
            .format(Texture.InternalFormat.RGB8)
            .sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .build(engine)
        val levels = texture.levels
        for (level in 0 until levels) {
            if (!loadCubeBitmap(engine, texture, level)) break
        }
        return texture
    }

    private fun loadCubeBitmap(
        engine: Engine,
        texture: Texture,
        level: Int = 0,
    ): Boolean {
        try {
            val faceSize = texture.getWidth(level) * texture.getHeight(level) * 4
            val storage = ByteBuffer.allocateDirect(faceSize * 6)
            loadBitmap().forEach {
                it.copyPixelsToBuffer(storage)
            }
            val buffer = PixelBufferDescriptor(
                storage, Texture.Format.RGB,
                Texture.Type.UINT_10F_11F_11F_REV
            )
            storage.flip()
            texture.setImage(
                engine,
                level,
                0,
                0,
                0,
                texture.getWidth(level),
                texture.getHeight(level),
                1,
                buffer
            )
            return true
        } catch (e: Exception) {
            LogAbility.e(TAG, "loadCubeBitmap:", e)
        }
        return false
    }

    private fun loadBitmap(): ArrayList<Bitmap> {
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

    private fun readUncompressedAsset(assetName: String): ByteBuffer {
        assets.openFd(assetName).use { fd ->
            val input = fd.createInputStream()
            val dst = ByteBuffer.allocate(fd.length.toInt())

            val src = Channels.newChannel(input)
            src.read(dst)
            src.close()

            return dst.apply { rewind() }
        }
    }

    private fun loadMaterial() {
        readUncompressedAsset("materials/lit.filamat").let {
            mMaterial = Material.Builder().payload(it, it.remaining()).build(mEngine)
        }
    }

    private fun createMesh() {
        val intSize = 4
        val floatSize = 4
        val shortSize = 2
        // A vertex is a position + a color:
        // 3 floats for XYZ position, 1 integer for color
        val vertexSize = 3 * floatSize + intSize

        // Define a vertex and a function to put a vertex in a ByteBuffer
        data class Vertex(val x: Float, val y: Float, val z: Float, val color: Int)

        fun ByteBuffer.put(v: Vertex): ByteBuffer {
            putFloat(v.x)
            putFloat(v.y)
            putFloat(v.z)
            putInt(v.color)
            return this
        }

        // We are going to generate a single triangle
        val vertexCount = 3
        val a1 = PI * 2.0 / 3.0
        val a2 = PI * 4.0 / 3.0

        val vertexData = ByteBuffer.allocate(vertexCount * vertexSize)
            // It is important to respect the native byte order
            .order(ByteOrder.nativeOrder())
            .put(Vertex(1.0f, 0.0f, 0.0f, 0xffff0000.toInt()))
            .put(Vertex(cos(a1).toFloat(), sin(a1).toFloat(), 0.0f, 0xff00ff00.toInt()))
            .put(Vertex(cos(a2).toFloat(), sin(a2).toFloat(), 0.0f, 0xff0000ff.toInt()))
            // Make sure the cursor is pointing in the right place in the byte buffer
            .flip()

        // Declare the layout of our mesh
        val vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            // Because we interleave position and color data we must specify offset and stride
            // We could use de-interleaved data by declaring two buffers and giving each
            // attribute a different buffer index
            .attribute(
                VertexBuffer.VertexAttribute.POSITION,
                0,
                VertexBuffer.AttributeType.FLOAT3,
                0,
                vertexSize
            )
            .attribute(
                VertexBuffer.VertexAttribute.COLOR,
                0,
                VertexBuffer.AttributeType.UBYTE4,
                3 * floatSize,
                vertexSize
            )
            // We store colors as unsigned bytes but since we want values between 0 and 1
            // in the material (shaders), we must mark the attribute as normalized
            .normalized(VertexBuffer.VertexAttribute.COLOR)
            .build(mEngine)

        // Feed the vertex data to the mesh
        // We only set 1 buffer because the data is interleaved
        vertexBuffer.setBufferAt(mEngine, 0, vertexData)

        // Create the indices
        val indexData = ByteBuffer.allocate(vertexCount * shortSize)
            .order(ByteOrder.nativeOrder())
            .putShort(0)
            .putShort(1)
            .putShort(2)
            .flip()

        val indexBuffer = IndexBuffer.Builder()
            .indexCount(3)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(mEngine)
        indexBuffer.setBuffer(mEngine, indexData)
    }
}