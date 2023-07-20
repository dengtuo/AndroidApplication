package com.wuba.android.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration

class ClientApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        InitProcessManager.onCreate(this)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        InitProcessManager.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        InitProcessManager.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        InitProcessManager.onTrimMemory(level)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        InitProcessManager.attachBaseContext(base)
    }
}