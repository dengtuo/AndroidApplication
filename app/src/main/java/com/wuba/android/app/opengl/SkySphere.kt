package com.wuba.android.app.opengl

import android.opengl.GLES20
import android.util.Pair
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin


class SkySphere(private val radius: Float) {

//    private val mVertices: ArrayList<FloatBuffer> = ArrayList()
//    private val mTextureCoords: ArrayList<FloatBuffer> = java.util.ArrayList()

    private val COORDS_PER_VERTEX = 3
    private val TEXTURE_COORDS_PER_VERTEX = 2

    private var mVertices: FloatBuffer? = null
    private var mVerticesCount: Int = 0
    private var mTextureCoords: FloatBuffer? = null

    init {
        calculateAttribute()
    }



    fun calculateSphereVertices() {
        val vertices = ArrayList<Float>()
        val textureVertixs = ArrayList<Float>()
        val angleSpan = Math.PI / 90
        var vAngle = 0.0
        while (vAngle < Math.PI) {
            var hAngle = 0.0
            while (hAngle < Math.PI * 2) {
                //四边形顶点  0  1  2 3  左上   右上  右下  左下
                val x0 = radius * sin(vAngle) * cos(hAngle)
                val y0 = radius * sin(vAngle) * sin(hAngle)
                val z0 = radius * cos(vAngle)

                val x1 = radius * sin(vAngle) * cos(hAngle + angleSpan)
                val y1 = radius * sin(vAngle) * sin(hAngle + angleSpan)
                val z1 = radius * cos(vAngle)

                val x2 = radius * sin(vAngle + angleSpan) * cos(hAngle + angleSpan)
                val y2 = radius * sin(vAngle + angleSpan) * sin(hAngle + angleSpan)
                val z2 = radius * cos(vAngle + angleSpan)

                val x3 = radius * sin(vAngle + angleSpan) * cos(hAngle)
                val y3 = radius * sin(vAngle + angleSpan) * sin(hAngle)
                val z3 = radius * cos(vAngle + angleSpan)

                //UV坐标
                val u0 = hAngle / (Math.PI * 2)
                val u1 = (hAngle + angleSpan) / (Math.PI * 2)
                val v0 = vAngle / Math.PI
                val v1 = (vAngle + angleSpan) / Math.PI


                //0->3->1
                vertices.add(x0.toFloat())
                vertices.add(y0.toFloat())
                vertices.add(z0.toFloat())
                textureVertixs.add(u0.toFloat())
                textureVertixs.add(v0.toFloat())

                vertices.add(x3.toFloat())
                vertices.add(y3.toFloat())
                vertices.add(z3.toFloat())
                textureVertixs.add(u0.toFloat())
                textureVertixs.add(v1.toFloat())

                vertices.add(x1.toFloat())
                vertices.add(y1.toFloat())
                vertices.add(z1.toFloat())
                textureVertixs.add(u1.toFloat())
                textureVertixs.add(v0.toFloat())

                //1->3->2
                vertices.add(x1.toFloat())
                vertices.add(y1.toFloat())
                vertices.add(z1.toFloat())
                textureVertixs.add(u1.toFloat())
                textureVertixs.add(v0.toFloat())

                vertices.add(x3.toFloat())
                vertices.add(y3.toFloat())
                vertices.add(z3.toFloat())
                textureVertixs.add(u0.toFloat())
                textureVertixs.add(v1.toFloat())

                vertices.add(x2.toFloat())
                vertices.add(y2.toFloat())
                vertices.add(z2.toFloat())
                textureVertixs.add(u1.toFloat())
                textureVertixs.add(v1.toFloat())


                hAngle += angleSpan
            }
            vAngle += angleSpan
        }
        mVerticesCount = vertices.size / 3
        mVertices = makeFloatBufferFromArray(vertices.toFloatArray())
        mTextureCoords = makeFloatBufferFromArray(textureVertixs.toFloatArray())
    }


    private fun calculateAttribute() {

        val vertices = ArrayList<Float>()
        val textureVertixs = ArrayList<Float>()
        val angleSpan = Math.PI / 90
        var vAngle = 0.0
        while (vAngle < Math.PI) {
            var hAngle = 0.0
            while (hAngle < Math.PI * 2) {
                //四边形顶点  0  1  2 3  左上   右上  右下  左下
                val x0 = radius * sin(vAngle) * cos(hAngle)
                val y0 = radius * sin(vAngle) * sin(hAngle)
                val z0 = radius * cos(vAngle)

                val x1 = radius * sin(vAngle) * cos(hAngle + angleSpan)
                val y1 = radius * sin(vAngle) * sin(hAngle + angleSpan)
                val z1 = radius * cos(vAngle)

                val x2 = radius * sin(vAngle + angleSpan) * cos(hAngle + angleSpan)
                val y2 = radius * sin(vAngle + angleSpan) * sin(hAngle + angleSpan)
                val z2 = radius * cos(vAngle + angleSpan)

                val x3 = radius * sin(vAngle + angleSpan) * cos(hAngle)
                val y3 = radius * sin(vAngle + angleSpan) * sin(hAngle)
                val z3 = radius * cos(vAngle + angleSpan)

                //UV坐标
                val u0 = hAngle / (Math.PI * 2)
                val u1 = (hAngle + angleSpan) / (Math.PI * 2)
                val v0 = vAngle / Math.PI
                val v1 = (vAngle + angleSpan) / Math.PI

                vertices.add(x1.toFloat())
                vertices.add(y1.toFloat())
                vertices.add(z1.toFloat())
                vertices.add(x0.toFloat())
                vertices.add(y0.toFloat())
                vertices.add(z0.toFloat())
                vertices.add(x3.toFloat())
                vertices.add(y3.toFloat())
                vertices.add(z3.toFloat())


                vertices.add(u1.toFloat())// x1 y1对应纹理坐标
                vertices.add(v0.toFloat())
                vertices.add(u0.toFloat())// x0 y0对应纹理坐标
                vertices.add(v0.toFloat())
                vertices.add(u0.toFloat())// x3 y3对应纹理坐标
                vertices.add(v1.toFloat())

                vertices.add(x1.toFloat())
                vertices.add(y1.toFloat())
                vertices.add(z1.toFloat())
                vertices.add(x3.toFloat())
                vertices.add(y3.toFloat())
                vertices.add(z3.toFloat())
                vertices.add(x2.toFloat())
                vertices.add(y2.toFloat())
                vertices.add(z2.toFloat())

                textureVertixs.add(u1.toFloat())// x1 y1对应纹理坐标
                textureVertixs.add(v0.toFloat())
                textureVertixs.add(u0.toFloat())// x3 y3对应纹理坐标
                textureVertixs.add(v1.toFloat())
                textureVertixs.add(u1.toFloat())// x2 y3对应纹理坐标
                textureVertixs.add(v1.toFloat())

                hAngle += angleSpan
            }
            vAngle += angleSpan
        }
        mVerticesCount = vertices.size / 3
        mVertices = makeFloatBufferFromArray(vertices.toFloatArray())
        mTextureCoords = makeFloatBufferFromArray(textureVertixs.toFloatArray())
    }

    fun draw(mPositionHandle: Int, mUVHandle: Int) {
        mVertices ?: return
        mTextureCoords ?: return
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertices)
        GLES20.glEnableVertexAttribArray(mUVHandle)
        GLES20.glVertexAttribPointer(mUVHandle, TEXTURE_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mTextureCoords)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVerticesCount)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mUVHandle)
    }


    private fun makeFloatBufferFromArray(array: FloatArray): FloatBuffer {
        val fb = ByteBuffer.allocateDirect(array.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        fb.put(array)
        fb.position(0)
        return fb
    }


    private fun convertToFloatBuffer(data: ArrayList<Float>): FloatBuffer {
        val d = FloatArray(data.size)
        for (i in d.indices) {
            d[i] = data[i]
        }
        val buffer = ByteBuffer.allocateDirect(data.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        val ret = buffer.asFloatBuffer()
        ret.put(d)
        ret.position(0)
        return ret
    }

}