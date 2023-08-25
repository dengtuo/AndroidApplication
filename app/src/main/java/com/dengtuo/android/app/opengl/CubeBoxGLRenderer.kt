package com.dengtuo.android.app.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeBoxGLRenderer:GLSurfaceView.Renderer {

    private var mGLProgram: Int = -1
    private var mScreenAspectRatio: Float = 0f



    private var mVertexShaderCode = ""
    private var mFragmentShaderCode = ""


    private var mUVHandle = 0
    private var mTextureHandle = 0
    private var mProjectionMatrixHandle = 0
    private var mViewMatrixHandle = 0
    private var mModelMatrixHandle = 0
    private var mPositionHandle = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    // Cube纹理
    private var mTextureId = -1

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
        mUVHandle = GLES20.glGetAttribLocation(mGLProgram, "aUV")
        mPositionHandle = GLES20.glGetAttribLocation(mGLProgram, "aPosition")
        mProjectionMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uProjectionMatrix")
        mViewMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uViewMatrix")
        mModelMatrixHandle = GLES20.glGetUniformLocation(mGLProgram, "uModelMatrix")
        mTextureHandle = GLES20.glGetUniformLocation(mGLProgram, "uTexture")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mScreenAspectRatio = width.toFloat() / height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        if(mTextureId == -1){
            mTextureId = GLAbility.createTexture()
        }
    }
}