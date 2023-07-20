package com.wuba.android.app

import android.app.Application
import android.content.res.Configuration

interface IModuleApplication {
    fun onCreate(application: Application)

    fun onTerminate()

    fun onConfigurationChanged(newConfig: Configuration?)

    fun onLowMemory()

    fun onTrimMemory(level: Int)
}