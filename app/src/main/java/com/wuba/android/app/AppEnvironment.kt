package com.wuba.android.app

import android.app.Application

object AppEnvironment {

    @JvmStatic
    var application: Application? = null

    @JvmStatic
    var isRelease: Boolean = true
}