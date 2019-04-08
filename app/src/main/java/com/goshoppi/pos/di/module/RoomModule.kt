package com.goshoppi.pos.di.module

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.dao.*
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepositoryImp
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepositoryImpl
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepositoryImpl
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepositoryImpl
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepositoryImpl
import com.goshoppi.pos.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(mApplication: Application) {


    //    val factory = SafeHelperFactory.fromUser(SpannableStringBuilder("encryptDb"))
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
    private val appDatabase = Room.databaseBuilder(
        mApplication,
        AppDatabase::class.java,
        DATABASE_NAME
    )
//        .openHelperFactory(factory)
        .allowMainThreadQueries()
        .addMigrations(MIGRATION_1_2)
        .build()

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
    fun provideCustomerDao(): LocalCustomerDao {
        return appDatabase.localCustomerDao()
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
    internal fun providesLocalProductRepository(localProductDao: LocalProductDao): LocalProductRepository {
        return LocalProductRepositoryImpl(localProductDao)
    }

    @Singleton
    @Provides
    fun providesMasterVariantDao(): MasterVariantDao {
        return appDatabase.masterVariantDao()
    }

    @Singleton
    @Provides
    fun providesLocalVariantDao(): LocalVariantDao {
        return appDatabase.localVariantDao()
    }

    @Singleton
    @Provides
    internal fun providesMasterVariantRepository(masterVariantDao: MasterVariantDao): MasterVariantRepository {
        return MasterVariantRepositoryImpl(masterVariantDao)
    }

    @Singleton
    @Provides
    internal fun providesLocalVariantRepository(localVariantDao: LocalVariantDao): LocalVariantRepository {
        return LocalVariantRepositoryImpl(localVariantDao)
    }

    @Singleton
    @Provides
    internal fun providesCustomerRepository(customertDao: LocalCustomerDao):CustomerRepository{
        return CustomerRepositoryImp(customertDao)
    }
}