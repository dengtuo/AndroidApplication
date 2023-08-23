package com.dengtuo.android.app.utis.log
interface ILogExtend : ILog {
    fun i(tag: String?, msg: String?, tr: Throwable?)
    fun d(tag: String?, msg: String?, tr: Throwable?)
    fun v(tag: String?, msg: String?, tr: Throwable?)
    fun w(tag: String?, msg: String?, tr: Throwable?)
    fun e(tag: String?, msg: String?, tr: Throwable?)

    fun setDebug(debug: Boolean)

    fun setTag(tag: String)
}