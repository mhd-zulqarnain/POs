package com.goshoppi.pos.app

import android.app.Application
import com.goshoppi.pos.BuildConfig
import timber.log.Timber.DebugTree
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}