package com.dengtuo.android.app.utis

import android.database.Cursor
import com.dengtuo.android.app.utis.log.LogAbility
import java.io.Closeable
import java.io.IOException

object CloseAbility {

    @JvmStatic
    fun close(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (null != closeable) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    LogAbility.d("CloseAbility", "close is exception", e)
                }
            }
        }
    }

    @JvmStatic
    fun close(cursor: Cursor?) {
        if (cursor != null && !cursor.isClosed) {
            try {
                cursor.close()
            } catch (e: Exception) {
                LogAbility.d("CloseAbility", "close is exception", e)
            }
        }
    }
}