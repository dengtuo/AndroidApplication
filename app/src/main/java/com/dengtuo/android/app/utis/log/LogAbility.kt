package com.dengtuo.android.app.utis.log

import java.util.concurrent.ConcurrentHashMap

object LogAbility {
    private val sLogger: ILogExtend

    private val mLoggerImplCaches = ConcurrentHashMap<Int, ILogExtend?>()

    init {
        sLogger = obtainLog(0)
    }

    @JvmStatic
    fun i(tag: String?, msg: String?) {
        sLogger.i(tag, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: String?) {
        sLogger.d(tag, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?) {
        sLogger.v(tag, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: String?) {
        sLogger.w(tag, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: String?) {
        sLogger.e(tag, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: String?, tr: Throwable?) {
        sLogger.i(tag, msg, tr)
    }

    @JvmStatic
    fun d(tag: String?, msg: String?, tr: Throwable?) {
        sLogger.d(tag, msg, tr)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?, tr: Throwable?) {
        sLogger.v(tag, msg, tr)
    }

    @JvmStatic
    fun w(tag: String?, msg: String?, tr: Throwable?) {
        sLogger.w(tag, msg, tr)
    }

    @JvmStatic
    fun e(tag: String?, msg: String?, tr: Throwable?) {
        sLogger.e(tag, msg, tr)
    }

    @JvmStatic
    fun setEnable(boolean: Boolean) {
        sLogger.setDebug(boolean)
    }

    @JvmStatic
    fun obtainLog(category: Int): ILogExtend {
        val logExtend = mLoggerImplCaches[category]
        if (logExtend != null) {
            return logExtend
        }
        val logger = InternalLoggerImpl()
        logger.setILog(null)
        mLoggerImplCaches[category] = logger
        return logger
    }
}