package com.dengtuo.android.app.opengl

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.dengtuo.android.app.R
import com.dengtuo.android.app.utis.FileAbility
import kotlin.math.abs

class GLSurfaceRenderView : GLSurfaceView {

    private var mSkySphereGLRenderer: SkySphereGLRenderer? = null
    private var mCubeBoxGLRenderer: CubeBoxGLRenderer? = null
    private var mGestureDetector: GestureDetector? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null

    constructor(context: Context?) : super(context) {

        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        initialize()
    }

//    private fun initialize() {
//        setEGLContextClientVersion(2)
//        mGLSurfaceRenderer = GLSurfaceRenderer()
//        setRenderer(mGLSurfaceRenderer)
//        mGestureDetector = GestureDetector(context, mGestureDetectorListener)
//        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureDetectorListener)
//    }
//

    //
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//
//        event ?: return false
//        if (mGestureDetector?.onTouchEvent(event) == true) {
//            return true
//        }
//        if (mScaleGestureDetector?.onTouchEvent(event) == true) {
//            return true
//        }
//        return super.onTouchEvent(event)
//    }
    fun setBitmap(bitmap: Bitmap) {
        mSkySphereGLRenderer?.setBitmap(bitmap)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var ret = false
        mScaleGestureDetector?.onTouchEvent(event)
        if (mScaleGestureDetector?.isInProgress == false) {
            ret = mGestureDetector?.onTouchEvent(event) == true
            if (!ret) {
                super.onTouchEvent(event)
            }
        }
        return ret
    }

    private fun initialize() {
        setEGLContextClientVersion(2)
        initSkySphere()
        //  initCubeBox()
        isLongClickable = true
        mGestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            private val SWIPE_MAX_OF_PATH_X = 100
            private val SWIPE_MAX_OF_PATH_Y = 100
            val handler = Handler()
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent) {
                return
            }

            override fun onLongPress(e: MotionEvent) {
                return
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ): Boolean {
                var ret = false
                if (abs(distanceX) > SWIPE_MAX_OF_PATH_X || abs(distanceY) > SWIPE_MAX_OF_PATH_Y) {
                    ret = false
                } else {
                    var diffX = distanceX / Constants.ON_SCROLL_DIVIDER_X
                    var diffY = distanceY / Constants.ON_SCROLL_DIVIDER_Y
                    if (abs(diffX) < Constants.THRESHOLD_SCROLL_X) {
                        diffX = 0.0f
                    }
                    if (abs(diffY) < Constants.THRESHOLD_SCROLL_Y) {
                        diffY = 0.0f
                    }
                    mSkySphereGLRenderer?.rotation(diffX, -diffY)
                    ret = true
                }
                return ret
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float,
            ): Boolean {
                return true
            }
        })
        mScaleGestureDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scale = detector.scaleFactor
                    mSkySphereGLRenderer?.scale(scale)
                    return true
                }
            })
        return
    }

    private fun initSkySphere() {

        val vertexShader =
            FileAbility.readAssetsString(context, "shader/sky_sphere_vertex_shader.glsl") ?: return
        val fragmentShader =
            FileAbility.readAssetsString(context, "shader/sky_sphere_fragment_shader.glsl")
                ?: return
        val skySphereGLRenderer = SkySphereGLRenderer()
        skySphereGLRenderer.initShader(vertexShader, fragmentShader)
        mSkySphereGLRenderer = skySphereGLRenderer
        setRenderer(skySphereGLRenderer)
    }

    private fun initCubeBox() {
        val vertexShader =
            FileAbility.readAssetsString(context, "shader/cube_box_vertex_shader.glsl") ?: return
        val fragmentShader =
            FileAbility.readAssetsString(context, "shader/cube_box_fragment_shader.glsl") ?: return
        val cubeBoxGLRenderer = CubeBoxGLRenderer()
        cubeBoxGLRenderer.initShader(vertexShader, fragmentShader)
        mCubeBoxGLRenderer = cubeBoxGLRenderer
        setRenderer(mCubeBoxGLRenderer)
    }


    private val mGestureDetectorListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float,
        ): Boolean {
            if (e1.pointerCount > 1 || e2.pointerCount > 1) {
                //双指以上放弃
                return false
            }
            mSkySphereGLRenderer?.rotation(distanceX, distanceY)
            return true
        }
    }

    private val mScaleGestureDetectorListener = object :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {

            return super.onScale(detector)
        }
    }
}