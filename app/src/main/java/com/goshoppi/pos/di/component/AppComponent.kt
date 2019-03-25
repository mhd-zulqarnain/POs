package com.goshoppi.pos.di.component

import android.app.Application
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.repository.ProductRepository
import com.goshoppi.pos.architecture.dao.ProductDao
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.view.PosMainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(dependencies = {}, modules = { AppModule.class, RoomModule .class })
interface AppComponent {

    fun inject(mainActivity: PosMainActivity)

    fun productDao(): ProductDao

    fun demoDatabase(): AppDatabase

    fun productRepository(): ProductRepository

    fun application(): Application

}