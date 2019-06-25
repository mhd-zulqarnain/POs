package com.goshoppi.pos.app

import com.facebook.stetho.Stetho
import com.goshoppi.pos.BuildConfig
import com.goshoppi.pos.di2.component.DaggerApplicationComponent

import com.goshoppi.pos.di2.module.RoomModule2
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber.DebugTree
import timber.log.Timber

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerApplicationComponent.builder().application(this).roomModule2(RoomModule2(this)).build()
        component.inject(this)
        return component
    }

    override fun onCreate() {
        //Debugging tool
        Stetho.initializeWithDefaults(this)
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}

