package com.goshoppi.pos.di.component

import android.app.Application
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.repository.local.LocalProductRepositoryImpl
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.architecture.repository.local.LocalProductRepository
import com.goshoppi.pos.architecture.repository.master.MasterProductRepository
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.view.PosMainActivity
import dagger.Component
import javax.inject.Singleton



@Singleton
@Component(modules = [AppModule::class, RoomModule::class])
interface AppComponent {

    fun injectPosMainActivity(mainActivity: PosMainActivity)

    fun getDatabase(): AppDatabase

//    fun getMasterProductDao(): MasterProductDao
//
//    fun getLocalProductDao(): LocalProductDao

    fun getLocalProductRepository(): LocalProductRepository

    fun getMasterProductRepository(): MasterProductRepository

    fun application(): Application

}