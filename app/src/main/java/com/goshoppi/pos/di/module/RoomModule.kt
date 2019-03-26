package com.goshoppi.pos.di.module

import android.app.Application
import android.arch.persistence.room.Room
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.architecture.repository.local.LocalProductRepository
import com.goshoppi.pos.architecture.repository.local.LocalProductRepositoryImpl
import com.goshoppi.pos.architecture.repository.master.MasterProductRepository
import com.goshoppi.pos.architecture.repository.master.MasterProductRepositoryImpl
import com.goshoppi.pos.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(mApplication: Application) {


//    val factory = SafeHelperFactory.fromUser(SpannableStringBuilder("encryptDb"))

    val appDatabase = Room.databaseBuilder(
        mApplication,
        AppDatabase::class.java,
        DATABASE_NAME
    )
//        .openHelperFactory(factory)
        .allowMainThreadQueries().build()

    @Singleton
    @Provides
    internal fun providesRoomDatabase(): AppDatabase {
        return appDatabase
    }


    @Singleton
    @Provides
    fun providesMasterProductDao(): MasterProductDao {
        return appDatabase.masterProductDao()
    }

    @Singleton
    @Provides
    fun providesLocalProductDao(): LocalProductDao {
        return appDatabase.localProductDao()
    }

    @Singleton
    @Provides
    internal fun providesMasterProductRepository(masterProductDao: MasterProductDao): MasterProductRepository {
        return MasterProductRepositoryImpl(masterProductDao)
    }

    @Singleton
    @Provides
    internal fun providesLocalProductRepository( localProductDao:  LocalProductDao): LocalProductRepository {
        return  LocalProductRepositoryImpl(localProductDao)
    }


}