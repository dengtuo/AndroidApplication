package com.dengtuo.android.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.dengtuo.android.app.BuildConfig

object InitProcessManager : IModuleApplication {
    override fun onCreate(application: Application) {
        initOnceProcess(application)
    }

    override fun onTerminate() {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {

    }

    override fun onLowMemory() {

    }

    override fun onTrimMemory(level: Int) {

    }

    fun attachBaseContext(newBase: Context?) {

    }


    private fun initOnceProcess(application: Application) {
        initClientEnvironment(application)
    }

    private fun initClientEnvironment(application: Application) {
        //优先初始化 context  后续流程都用的到了context
        AppEnvironment.application = application
        AppEnvironment.isRelease = !BuildConfig.DEBUG
//        AppEnvironment.isRelease = false
    }
}