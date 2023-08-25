package com.dengtuo.android.app.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.dengtuo.android.app.utis.log.LogAbility
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object GLAbility {

    private const val TAG = "OpenGLAbility"

    @JvmStatic
    fun compileGLShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        //查看配置 是否成功
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) {
            //失败
            "load  shader:" + GLES20.glGetShaderInfoLog(shader)
        }
        return shader
    }

    @JvmStatic
    fun linkGLProgram(program: Int, vertexShader: Int, fragmentShader: Int) {
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        //获得状态

        //查看配置 是否成功
        val status = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) {
            "link program:" + GLES20.glGetProgramInfoLog(program)
        }
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }


    @JvmStatic
    fun createTexture(bitmap: Bitmap?): Int {
        val texture = IntArray(1)
        if (bitmap != null && !bitmap.isRecycled) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            /**配置纹理属性，主要是纹理采样边缘的处理 */
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat()
            )
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            if (!bitmap.isRecycled) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            }
            return texture[0]
        }
        return 0
    }


    /**
     * Texture setting method
     * @param texture Setting texture
     */
    fun createImageTexture(textures: IntArray?, bitmap: Bitmap?) {
        textures ?: return
        if (bitmap != null && !bitmap.isRecycled) {
            val dividedWidth = bitmap.width / 2
            GLES20.glGenTextures(textures.size, textures, 0)
            for (textureIndex in textures.indices) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[textureIndex])
                /**配置纹理属性，主要是纹理采样边缘的处理 */
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat()
                )
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
                )
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE.toFloat()
                )
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE.toFloat()
                )
                if (!bitmap.isRecycled) {
                    val dividedBitmap = Bitmap.createBitmap(
                        bitmap, dividedWidth * textureIndex, 0, dividedWidth, bitmap.height
                    )
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, dividedBitmap, 0)
                }
            }
        }
    }

    /**
     * 左右 0 1
     * 下上 2 3
     * 前后 4 5
     * @param array List<Bitmap>
     * @return Int
     */
    fun createCubeMapTexture(cubeBitmaps: List<Bitmap>): Int {
        if (cubeBitmaps.size != 6) {
            LogAbility.d(TAG, "createCubeMapTexture")
            return -1
        }
        val textures = IntArray(1)
        textures[0] = -1
        GLES20.glGenTextures(1, textures, 0)
        // Linear filtering for minification and magnification
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textures[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )
        //左
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0)
        //右
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0)
        //下
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0)
        //上
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0)
        //前
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0)
        //后
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0)
        return textures[0]
    }

    @JvmStatic
    fun makeFloatBufferFromArray(array: FloatArray): FloatBuffer {
        val fb =
            ByteBuffer.allocateDirect(array.size * Float.SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        fb.put(array)
        fb.position(0)
        return fb
    }


    @JvmStatic
    fun makeByteBufferFromArray(array: ByteArray): ByteBuffer? {
        val buffer = ByteBuffer.allocateDirect(array.size * Byte.SIZE_BITS)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(array)
        buffer.position(0)
        return buffer
    }
}