package com.dengtuo.android.app.utis

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever.BitmapParams
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.dengtuo.android.app.utis.log.LogAbility
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

object BitmapAbility {
    const val TYPE_JPEG = 0
    const val TYPE_PNG = 1
    const val TYPE_WEBP = 2
    private const val MAX_WIDTH = 4096
    private const val MAX_HEIGHT = MAX_WIDTH / 4 * 3
    private const val TAG = "BitmapUtils"

    /**
     * method for decode Bitmap into File(in type JPEG,PNG or WEBP)
     *
     * @param bitmap deocde orgin bitmap
     * @param path   save image file path, no need .jpeg, .png or .webp, the method will add auto
     *
     * @return result in boolean, true is success, false is fail
     */
    @JvmStatic
    fun decodeBitmapToFile(bitmap: Bitmap?, path: String?, type: Int, quality: Int): Boolean {
        if (bitmap == null || bitmap.isRecycled) {
            LogAbility.e(TAG, "decodeBitmapToFile fail bitmap is recycled or null")
            return false
        }
        if (TextUtils.isEmpty(path)) {
            LogAbility.e(TAG, "decodeBitmapToFile fail save path is empty")
            return false
        }
        val format = getDecodeType(type)
        val file = File(path)
        file.mkdirs()
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
        } catch (e: Exception) {
            LogAbility.e(TAG, "decodeBitmapToFile fail file is no exit or no a file")
            e.printStackTrace()
            return false
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            val result = bitmap.compress(format, quality, fos)
            LogAbility.d(TAG, "decodeBitmapToFile result: $result")
            return result
        } catch (e: Exception) {
            LogAbility.e(TAG, "decodeBitmapToFile fail for exception")
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }


    @JvmStatic
    fun decodeAssetsBitmap(
        context: Context?, fileName: String?,
        maxWidth: Int = MAX_WIDTH,
        maxHeight: Int = MAX_HEIGHT,
    ): Bitmap? {
        fileName ?: return null
        context ?: return null
        var bitmap: Bitmap?
        var inputStream:InputStream? = null
        try {
            val originOption = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            inputStream = context.assets.open(fileName)
            decodeBitmapOptions(inputStream, originOption)
            inputStream = context.assets.open(fileName)
            val outWidth = originOption.outWidth
            val outHeight = originOption.outHeight
            if (outWidth > maxWidth || outHeight > maxHeight) {
                val sample = findBestSampleSize(
                    originOption.outWidth,
                    originOption.outHeight,
                    maxWidth,
                    maxHeight
                )
                bitmap =
                    BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options().apply {
                        inScaled = true
                        inSampleSize = sample
                        inDensity = originOption.outWidth
                        inTargetDensity = maxWidth * sample
                        inJustDecodeBounds = false
                    })
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            bitmap = null
        }finally {
            CloseAbility.close(inputStream)
        }
        return bitmap
    }


    /**
     * @param type support TYPE_PNG, TYPE_JPEG, TYPE_WEBP
     * but TYPE_WEBP only support version above android 4.0
     *
     * @return decode type for file
     */
    @JvmStatic
    private fun getDecodeType(type: Int): Bitmap.CompressFormat {
        var format = Bitmap.CompressFormat.JPEG
        when (type) {
            TYPE_PNG -> format = Bitmap.CompressFormat.PNG
            TYPE_WEBP -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                format = Bitmap.CompressFormat.WEBP
            } else {
                LogAbility.e(TAG, "getDecodeType fail for SDK version is low than 4.0")
            }

            else -> LogAbility.e(TAG, "getDecodeType fail for not in type")
        }
        return format
    }

    @JvmStatic
    fun formatPath(path: String, type: Int): String {
        var path = path
        if (TextUtils.isEmpty(path)) {
            return path
        }
        path = when (type) {
            TYPE_PNG -> "$path.png"
            TYPE_WEBP -> "$path.webp"
            else -> "$path.jpg"
        }
        return path
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap 位图
     * @param w      新的宽度
     * @param h      新的高度
     *
     * @return Bitmap
     */
    @JvmStatic
    fun zoomBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        return if (w == width && h == height) {
            bitmap
        } else Bitmap.createScaledBitmap(bitmap, w, h, true)
    }

    @JvmStatic
    fun cropBitmap(bitmap: Bitmap, cropWidth: Int, cropHeight: Int): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        return Bitmap.createBitmap(
            bitmap,
            (w - cropWidth) / 2,
            (h - cropHeight) / 2,
            cropWidth,
            cropHeight,
            null,
            false
        )
    }


    @JvmStatic
    fun decodeBitmap(
        context: Context?,
        uri: Uri?,
        maxWidth: Int = MAX_WIDTH,
        maxHeight: Int = MAX_HEIGHT,
    ): Bitmap? {
        uri ?: return null
        context ?: return null
        var bitmap: Bitmap?
        try {
            val originOption = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            decodeBitmapOptions(context, uri, originOption)
            val captureWidth = originOption.outWidth
            if (captureWidth > maxWidth) {
                val sample = findBestSampleSize(
                    originOption.outWidth,
                    originOption.outHeight,
                    maxWidth,
                    maxHeight
                )
                bitmap =
                    decodeBitmapData(context, uri, BitmapFactory.Options().apply {
                        inScaled = true
                        inSampleSize = sample
                        inDensity = originOption.outWidth
                        inTargetDensity = maxWidth * sample
                        inJustDecodeBounds = false
                    })
            } else {
                bitmap = decodeBitmapData(context, uri, null)
            }
        } catch (e: Exception) {
            bitmap = null
        }
        if (bitmap != null) {
            bitmap = maybeTransformBitmap(context.contentResolver, uri, bitmap)
        }
        return bitmap
    }

    private fun decodeBitmapOptions(context: Context, uri: Uri?, options: BitmapFactory.Options) {
        if (uri == null) {
            return
        }
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme) {
            var stream: InputStream? = null
            try {
                stream = context.contentResolver.openInputStream(uri)
                stream?.let { decodeBitmapOptions(it, options) }
            } catch (e: java.lang.Exception) {
                LogAbility.w(TAG, "Unable to open content: $uri", e)
            } finally {
                CloseAbility.close(stream)
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            LogAbility.e(TAG, "Unable to close content: $uri")
        } else {
            LogAbility.e(TAG, "Unable to close content: $uri")
        }
    }

    private fun decodeBitmapOptions(inputStream: InputStream, options: BitmapFactory.Options) {
        BitmapFactory.decodeStream(inputStream, null, options)
    }

    private fun decodeBitmapData(
        context: Context,
        uri: Uri?,
        options: BitmapFactory.Options?,
    ): Bitmap? {
        if (uri == null) {
            return null
        }
        var bitmap: Bitmap? = null
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme) {
            var stream: InputStream? = null
            try {
                stream = context.contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(stream, null, options)
            } catch (e: Exception) {
                LogAbility.e(TAG, "Unable to open content: $uri", e)
            } finally {
                CloseAbility.close(stream)
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE == scheme) {
            LogAbility.e(TAG, "Unable to close content: $uri")
        } else {
            LogAbility.e(TAG, "Unable to close content: $uri")
        }
        return bitmap
    }

    @JvmStatic
    fun decodeBitmap(
        path: String?,
        maxWidth: Int = MAX_WIDTH,
        maxHeight: Int = MAX_HEIGHT,
    ): Bitmap? {
        if (path?.isNotEmpty() == true) {
            var bitmap: Bitmap? = null
            try {
                val originOption = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(path, originOption)
                val captureWidth = originOption.outWidth
                if (captureWidth > maxWidth) {
                    val sample = findBestSampleSize(
                        originOption.outWidth,
                        originOption.outHeight,
                        maxWidth,
                        maxHeight
                    )
                    bitmap = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
                        inScaled = true
                        inSampleSize = sample
                        inDensity = originOption.outWidth
                        inTargetDensity = maxWidth * sample
                        inJustDecodeBounds = false
                    })
                } else {
                    bitmap = BitmapFactory.decodeFile(path)
                }
            } catch (e: Exception) {
                LogAbility.e(TAG, "compressBitmap:", e)
            }
            return bitmap
        }
        return null
    }

    private fun findBestSampleSize(
        actualWidth: Int, actualHeight: Int, desiredWidth: Int, desiredHeight: Int,
    ): Int {
        val wr = actualWidth.toDouble() / desiredWidth
        val hr = actualHeight.toDouble() / desiredHeight
        val ratio = min(wr, hr)
        var inSampleSize = 1.0f
        while (inSampleSize * 2 <= ratio) {
            inSampleSize *= 2f
        }
        return inSampleSize.toInt()
    }

    private fun rotateBitmap(
        bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean,
    ): Bitmap {
        val matrix = Matrix()
        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees.toFloat())
        // Mirror the image along the X or Y axis.
        matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        return rotatedBitmap
    }

    private fun maybeTransformBitmap(
        resolver: ContentResolver,
        uri: Uri,
        bitmap: Bitmap,
    ): Bitmap {
        val orientation: Int = getExifOrientationTag(resolver, uri)
        var rotationDegrees = 0
        var flipX = false
        var flipY = false
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipX = true
            ExifInterface.ORIENTATION_ROTATE_90 -> rotationDegrees = 90
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                rotationDegrees = 90
                flipX = true
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> rotationDegrees = 180
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipY = true
            ExifInterface.ORIENTATION_ROTATE_270 -> rotationDegrees = -90
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                rotationDegrees = -90
                flipX = true
            }

            ExifInterface.ORIENTATION_UNDEFINED, ExifInterface.ORIENTATION_NORMAL -> {}
            else -> {}
        }
        return rotateBitmap(bitmap, rotationDegrees, flipX, flipY)
    }


    private fun getExifOrientationTag(resolver: ContentResolver, imageUri: Uri): Int {
        if (ContentResolver.SCHEME_CONTENT != imageUri.scheme && ContentResolver.SCHEME_FILE != imageUri.scheme) {
            return ExifInterface.ORIENTATION_UNDEFINED
        }
        var exif: ExifInterface? = null
        try {
            resolver.openInputStream(imageUri)
                ?.use { inputStream -> exif = ExifInterface(inputStream) }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open file to read rotation meta data: $imageUri", e)
        }
        return exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
            ?: ExifInterface.ORIENTATION_UNDEFINED
    }
}