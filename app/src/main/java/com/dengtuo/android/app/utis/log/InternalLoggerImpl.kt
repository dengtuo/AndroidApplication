package com.dengtuo.android.app.utis.log

import android.util.Log

class InternalLoggerImpl : ILogExtend {
    @Volatile
    private var mEnable = true

    private var innerTag: String = APP_TAG

    @Volatile
    private var mLog: ILog? = null
    override fun i(tag: String?, msg: String?) {
        val log = "$tag#$msg"
        if (mEnable) {
            Log.i(innerTag, log)
        }
        mLog?.i(innerTag, log)
    }

    override fun d(tag: String?, msg: String?) {
        val log = "$tag#$msg"
        if (mEnable) {
            Log.d(innerTag, log)
        }
        mLog?.d(innerTag, log)
    }

    override fun v(tag: String?, msg: String?) {
        val log = "$tag#$msg"
        if (mEnable) {
            Log.v(innerTag, log)
        }
        mLog?.v(innerTag, log)
    }

    override fun w(tag: String?, msg: String?) {
        val log = "$tag#$msg"
        if (mEnable) {
            Log.w(innerTag, log)
        }
        mLog?.w(innerTag, log)
    }

    override fun e(tag: String?, msg: String?) {
        val log = "$tag#$msg"
        if (mEnable) {
            Log.e(innerTag, log)
        }
        mLog?.e(innerTag, log)
    }

    override fun i(tag: String?, msg: String?, tr: Throwable?) {
        i(
            tag, """
     $msg
     ${Log.getStackTraceString(tr)}
     """.trimIndent()
        )
    }

    override fun d(tag: String?, msg: String?, tr: Throwable?) {
        d(
            tag, """
     $msg
     ${Log.getStackTraceString(tr)}
     """.trimIndent()
        )
    }

    override fun v(tag: String?, msg: String?, tr: Throwable?) {
        v(
            tag, """
     $msg
     ${Log.getStackTraceString(tr)}
     """.trimIndent()
        )
    }

    override fun w(tag: String?, msg: String?, tr: Throwable?) {
        w(
            tag, """
     $msg
     ${Log.getStackTraceString(tr)}
     """.trimIndent()
        )
    }

    override fun e(tag: String?, msg: String?, tr: Throwable?) {
        e(tag, """$msg${Log.getStackTraceString(tr)}""".trimIndent())
    }

    override fun setDebug(debug: Boolean) {
        mEnable = debug
    }

    override fun setTag(tag: String) {
        innerTag = tag
    }

    fun setILog(iLog: ILog?) {
        mLog = iLog
    }

    companion object {
        private const val APP_TAG = "SaasApp"
    }
}