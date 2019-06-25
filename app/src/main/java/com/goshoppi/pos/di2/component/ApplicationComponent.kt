package com.goshoppi.pos.di2.component

import android.app.Application
import com.goshoppi.pos.app.BaseApplication
import com.goshoppi.pos.di2.module.ActivityBindingModule
import com.goshoppi.pos.di2.module.ApplicationModule
import com.goshoppi.pos.di2.module.ContextModule
import com.goshoppi.pos.di2.module.RoomModule2
import com.goshoppi.pos.di2.scope.AppScoped
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

import javax.inject.Singleton

@AppScoped
@Component(modules = [
    RoomModule2::class,
    ContextModule::class,
    ApplicationModule::class,
    AndroidSupportInjectionModule::class,
    ActivityBindingModule::class])
interface ApplicationComponent : AndroidInjector<DaggerApplication> {

    fun inject(application: BaseApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun roomModule2(roomModule2: RoomModule2): Builder
        fun build(): ApplicationComponent
    }
}