package com.goshoppi.pos.di.module

import com.goshoppi.pos.architecture.dao.ProductDao
import com.goshoppi.pos.architecture.repository.ProductRepository
import dagger.Provides
import javax.inject.Singleton
import android.app.Application
import com.goshoppi.pos.architecture.AppDatabase
import dagger.Module


@Module
class RoomModule(mApplication: Application) {

    private val appDatabase: AppDatabase

    init {
        appDatabase = AppDatabase.getInstance(mApplication)
    }

    @Singleton
    @Provides
    internal fun providesRoomDatabase(): AppDatabase {
        return appDatabase
    }

    @Singleton
    @Provides
    internal fun providesProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Singleton
    @Provides
    internal fun productRepository(productDao: ProductDao): ProductRepository {
        return ProductDataSource(productDao)
    }

}