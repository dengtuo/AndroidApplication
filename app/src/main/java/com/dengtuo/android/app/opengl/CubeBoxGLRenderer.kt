package com.dengtuo.android.app.opengl

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.dengtuo.android.app.utis.log.LogAbility
import java.nio.channels.FileChannel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CubeBoxGLRenderer : GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "TAG"
        private const val Z_NEAR = 0.001f
        private const val Z_FAR = 1000.0f
    }

    private var mCubeBox: CubeBox? = null

    private var mGLProgram: Int = -1
    private var mScreenAspectRatio: Float = 0f


    private var mVertexShaderCode = ""
    private var mFragmentShaderCode = ""


    private var mTextureHandle = 0
    private var mProjectionMatrixHandle = 0
    private var mViewMatrixHandle = 0
    private var mModelMatrixHandle = 0
    private var mPositionHandle = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    private val mCameraPositionX = 0.0f
    private val mCameraPositionY = 0.0f
    private val mCameraPositionZ = 0.0f
    private var mCameraDirectionX = 0.0f
    private var mCameraDirectionY = 0.0f
    private var mCameraDirectionZ = -1.0f
    private var mCameraFovDegree = 100f

    private var mRotationAngleY = 0.0
    private var mRotationAngleXZ = 0.0

    // Cube纹理
    private var mTextureId = -1

    private var mCubeBitmap: List<Bitmap>? = null

    fun initShader(vertexShaderCode: String, fragmentShaderCode: String) {
        mVertexShaderCode = vertexShaderCode
        mFragmentShaderCode = fragmentShaderCode
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mGLProgram = GLES20.glCreateProgram()
        val vertexShader = GLAbility.compileGLShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode)
        val fragmentShader = GLAbility.compileGLShader(
            GLES20.GL_FRAGMENT_SHADER,
            mFragmentShaderCode
        )
        GLAbility.linkGLProgram(mGLProgram, vertexShader, fragmentShader)
        GLES20.glUseProgram(mGLProgram)
        mPositionHandle = GLES20.glGetAttribLocation(mGLProgram, "a_Position")
        mProjectionMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uProjectionMatrix")
        mViewMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uViewMatrix")
        mModelMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uModelMatrix")
        mTextureHandle = GLES20.glGetUniformLocation(mGLProgram, "uTexture")
        mCubeBox = CubeBox()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mScreenAspectRatio = width.toFloat() / height
        GLES20.glViewport(0, 0, width, height)
        Matrix.setLookAtM(
            mViewMatrix,
            0,
            mCameraPositionX,
            mCameraPositionY,
            mCameraPositionZ,
            mCameraDirectionX,
            mCameraDirectionY,
            mCameraDirectionZ,
            0.0f,
            1.0f,
            0.0f
        )
        Matrix.perspectiveM(
            mProjectionMatrix,
            0,
            mCameraFovDegree,
            mScreenAspectRatio,
            Z_NEAR,
            Z_FAR
        )
    }

    private fun createBitmap() {
        /**
         * 左右 0 1
         * 下上 2 3
         * 前后 4 5
         * @param array List<Bitmap>
         * @return Int
         */
        val bitmaps = ArrayList<Bitmap>()
        val width = 255
        val height = 255
        val random = Random(1)
        val colorArray = arrayOf(Color.BLUE,Color.WHITE,Color.YELLOW,Color.RED,Color.CYAN,Color.GREEN)
        for (index in 0 until 6) {
            val colors = ArrayList<Int>()
            for (i in 0 until width) {
                for (j in 0 until height) {
//                    colors.add(Color.argb(255,random.nextInt(0,255),random.nextInt(0,255),random.nextInt(0,255)))
                    colors.add(colorArray[index])
                }
            }
            bitmaps.add(
                Bitmap.createBitmap(
                    colors.toIntArray(),
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )
            )
        }
        mCubeBitmap = bitmaps
    }

    override fun onDrawFrame(gl: GL10?) {
        if (mTextureId == -1) {
         //   createBitmap()
            mCubeBitmap?.let {
                mTextureId = GLAbility.createCubeMapTexture(it)
            }
            LogAbility.d(TAG, "")
        }
        //矩阵计算
        calculateMatrix()
        //将矩阵值传入shader
        GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0)
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0)
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0)
        //draw
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mTextureId)
        mCubeBox?.draw(mPositionHandle)
    }

    private fun calculateMatrix() {
        mCameraDirectionX = (cos(mRotationAngleXZ) * cos(mRotationAngleY)).toFloat()
        mCameraDirectionY = sin(mRotationAngleY).toFloat()
        mCameraDirectionZ = (sin(mRotationAngleXZ) * cos(mRotationAngleY)).toFloat()

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //初始化矩阵
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.setIdentityM(mProjectionMatrix, 0)

        //设置矩阵转化 视角
        Matrix.setLookAtM(
            mViewMatrix,
            0,
            mCameraPositionX,
            mCameraPositionY,
            mCameraPositionZ,
            mCameraDirectionX,
            mCameraDirectionY,
            mCameraDirectionZ,
            0.0f,
            1.0f,
            0.0f
        )
        Matrix.perspectiveM(
            mProjectionMatrix,
            0,
            mCameraFovDegree,
            mScreenAspectRatio,
            Z_NEAR,
            Z_FAR
        )
    }

    fun setCubeBitmap(cubeBitmap: List<Bitmap>) {
        mCubeBitmap = cubeBitmap
    }

    fun rotation(xz: Float, y: Float) {
        mRotationAngleXZ += xz.toDouble()
        mRotationAngleY += y.toDouble()
        if (mRotationAngleY > Math.PI / 2) {
            mRotationAngleY = Math.PI / 2
        }
        if (mRotationAngleY < -(Math.PI / 2)) {
            mRotationAngleY = -(Math.PI / 2)
        }
        return
    }

    fun scale(ratio: Float) {

    }
}