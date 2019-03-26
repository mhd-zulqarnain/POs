package com.goshoppi.pos.di.module

import android.app.Application
import dagger.Module
import javax.inject.Singleton
import dagger.Provides

@Module
class AppModule(internal var mApplication: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return mApplication
    }

}