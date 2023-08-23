package com.dengtuo.android.app.utis.log

interface ILog {
    fun i(tag: String?, msg: String?)
    fun d(tag: String?, msg: String?)
    fun v(tag: String?, msg: String?)
    fun w(tag: String?, msg: String?)
    fun e(tag: String?, msg: String?)
}