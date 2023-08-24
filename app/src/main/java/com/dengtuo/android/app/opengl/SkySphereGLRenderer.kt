package com.dengtuo.android.app.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class SkySphereGLRenderer : GLSurfaceView.Renderer {

    private var mUVHandle = 0
    private var mTextureHandle = 0
    private var mProjectionMatrixHandle = 0
    private var mViewMatrixHandle = 0
    private var mModelMatrixHandle = 0
    private var mPositionHandle = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)
    private val mSkySphere = SkySphere(2.0f)
    private var mGLProgram: Int = 0
    private var mTextureId = -1
    private var mRotationAngleY = 0.0
    private var mRotationAngleXZ = 0.0
    private var mScreenAspectRatio = 0f
    private val mCameraPositionX = 0.0f
    private val mCameraPositionY = 0.0f
    private val mCameraPositionZ = 0.0f
    private var mCameraDirectionX = 0.0f
    private var mCameraDirectionY = 0.0f
    private var mCameraDirectionZ = 1.0f
    private var mCameraFovDegree = 100f
    private var mVertexShaderCode = ""
    private var mFragmentShaderCode = ""

    private var mBitmap: Bitmap? = null

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
    }

    fun initShader(vertexShaderCode: String, fragmentShaderCode: String) {
        mVertexShaderCode = vertexShaderCode
        mFragmentShaderCode = fragmentShaderCode
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        mGLProgram = GLES20.glCreateProgram()
        val vertexShader = OpenGLAbility.compileGLShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode)
        val fragmentShader = OpenGLAbility.compileGLShader(
            GLES20.GL_FRAGMENT_SHADER,
            mFragmentShaderCode
        )
        OpenGLAbility.linkGLProgram(mGLProgram, vertexShader, fragmentShader)
        GLES20.glUseProgram(mGLProgram)
        mUVHandle = GLES20.glGetAttribLocation(mGLProgram, "aUV")
        mPositionHandle = GLES20.glGetAttribLocation(mGLProgram, "aPosition")
        mProjectionMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uProjectionMatrix")
        mViewMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uViewMatrix")
        mModelMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uModelMatrix")
        mTextureHandle = GLES20.glGetUniformLocation(mGLProgram, "uTexture")
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
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


    override fun onDrawFrame(gl: GL10) {
        val bitmap = mBitmap ?: return
        if (bitmap.isRecycled) {
            return
        }
        if (mTextureId == -1) {
            mTextureId = OpenGLAbility.createTexture(bitmap)
        }

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
        Matrix.rotateM(mModelMatrix, 0, 90f, 0f, 0f, 1f)
        Matrix.rotateM(mModelMatrix, 0, 90f, 1f, 0f, 0f)
        Matrix.rotateM(mModelMatrix, 0, 90f, 0f, 1f, 0f)

        //将矩阵值传入shader
        GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0)
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0)
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0)

        //draw
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        mSkySphere.draw(mPositionHandle, mUVHandle)
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

    companion object {
        private const val Z_NEAR = 0.1f
        private const val Z_FAR = 100.0f
    }
}