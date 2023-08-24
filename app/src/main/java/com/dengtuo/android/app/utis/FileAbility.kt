package com.dengtuo.android.app.utis

import android.content.Context
import android.content.res.Resources.NotFoundException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object FileAbility {

    @JvmStatic
    fun readAssetsString(context: Context, fileName: String): String? {
        val inputStream = context.assets.open(fileName)
        return readInputStream(inputStream)
    }

    @JvmStatic
    fun readInputStream(inputStream: InputStream?): String? {
        return inputStream?.use { input ->
            InputStreamReader(input).use { inputStreamReader ->
                BufferedReader(inputStreamReader).use { bufferedReader ->
                    var nextLine: String?
                    val body = StringBuilder()
                    while (bufferedReader.readLine().also { nextLine = it } != null) {
                        body.appendLine(nextLine)
                    }
                    body.toString()
                }
            }
        }
    }

    fun readTextFileFromResource(
        context: Context,
        resourceId: Int,
    ): String? {
        val body = java.lang.StringBuilder()
        try {
            val inputStream = context.resources
                .openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(
                inputStream
            )
            val bufferedReader = BufferedReader(
                inputStreamReader
            )
            var nextLine: String?
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException(
                "Could not open resource: $resourceId", e
            )
        } catch (nfe: NotFoundException) {
            throw RuntimeException(
                "Resource not found: "
                        + resourceId, nfe
            )
        }
        return body.toString()
    }
}