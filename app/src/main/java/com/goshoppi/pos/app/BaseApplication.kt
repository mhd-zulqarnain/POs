package com.goshoppi.pos.app

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.goshoppi.pos.BuildConfig
import com.goshoppi.pos.di2.component.DaggerApplicationComponent

import com.goshoppi.pos.di2.module.RoomModule2
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber.DebugTree
import timber.log.Timber

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        val component = DaggerApplicationComponent.
            builder().
            application(this).
            roomModule2(RoomModule2(this)).
            build()
        component.inject(this)
        return component
    }

    override fun onCreate() {
        //Debugging tool
        Stetho.initializeWithDefaults(this)
        super.onCreate()
        val core = CrashlyticsCore
            .Builder()
            .listener {
                Log.d("****************", "Crash happened")
            }
            .build()

        val crashlyticsKit = Crashlytics
            .Builder()
            .core(core)
            .build()
        Fabric.with(this,crashlyticsKit)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}

