package com.dengtuo.android.app.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class CubeBox {

    private var mVertexBuffer: FloatBuffer? = null
    private var mIndexBuffer: ByteBuffer? = null

    private val cubeCoords = floatArrayOf(
        -1f, 1f, 1f,  // (0) Top-left near
        1f, 1f, 1f,  // (1) Top-right near
        -1f, -1f, 1f,  // (2) Bottom-left near
        1f, -1f, 1f,  // (3) Bottom-right near
        -1f, 1f, -1f,  // (4) Top-left far
        1f, 1f, -1f,  // (5) Top-right far
        -1f, -1f, -1f,  // (6) Bottom-left far
        1f, -1f, -1f // (7) Bottom-right far
    )

    private val cubeIndexs = byteArrayOf(
        // Front
        0, 2, 1,
        1, 2, 3,

        // Back
        5, 7, 4,
        4, 7, 6,

        // Left
        4, 6, 0,
        0, 6, 2,

        // Right
        1, 3, 5,
        5, 3, 7,

        // Top
        4, 0, 5,
        5, 0, 1,

        // Bottom
        6, 2, 7,
        7, 2, 3
    )

    private val cubeIndexs2 = byteArrayOf( // Front
        1, 3, 0,
        0, 3, 2,  // Back
        4, 6, 5,
        5, 6, 7,  // Left
        0, 2, 4,
        4, 2, 6,  // Right
        5, 7, 1,
        1, 7, 3,  // Top
        5, 1, 4,
        4, 1, 0,  // Bottom
        6, 2, 7,
        7, 2, 3
    )

    init {
        mVertexBuffer = GLAbility.makeFloatBufferFromArray(cubeCoords)
        mIndexBuffer = GLAbility.makeByteBufferFromArray(cubeIndexs)
    }

    fun draw(mPositionHandle: Int) {
        mVertexBuffer ?: return
        mIndexBuffer ?: return
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, 0, mVertexBuffer
        )
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, COORDS_INDEX_COUNT, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val COORDS_INDEX_COUNT = 3

    }
}