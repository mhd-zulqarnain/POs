package com.goshoppi.pos.di2.module

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkerFactory
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.dao.*
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepositoryImp
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepositoryImp
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepositoryImp
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepositoryImp
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepositoryImpl
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepositoryImpl
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepositoryImpl
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepositoryImpl
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepository
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepositoryImp
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepository
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepositoryImp
import com.goshoppi.pos.architecture.repository.userRepo.UserRepository
import com.goshoppi.pos.architecture.repository.userRepo.UserRepositoryImp
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.di2.workmanager.utils.DaggerWorkerFactory
import com.goshoppi.pos.utils.Constants.BASE_URL
import com.goshoppi.pos.utils.Constants.DATABASE_NAME
import com.goshoppi.pos.webservice.retrofit.MyServices
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module(includes = [ViewModelModule::class])
class RoomModule2(mApplication: Application) {


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
//        .allowMainThreadQueries()
        .addMigrations(MIGRATION_1_2)
        .build()

    @AppScoped
    @Provides
    internal fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @AppScoped
    @Provides
    internal fun provideRetrofitService(retrofit: Retrofit): MyServices {
        return retrofit.create(MyServices::class.java)
    }

    @AppScoped
    @Provides
    internal fun providesRoomDatabase(): AppDatabase {
        return appDatabase
    }

    @AppScoped
    @Provides
    fun providesMasterProductDao(): MasterProductDao {
        return appDatabase.masterProductDao()
    }

    @AppScoped
    @Provides
    fun provideCustomerDao(): LocalCustomerDao {
        return appDatabase.localCustomerDao()
    }

    @AppScoped
    @Provides
    fun provideUserDao(): UserDao {
        return appDatabase.UserDao()
    }

    @AppScoped
    @Provides
    fun providesLocalProductDao(): LocalProductDao {
        return appDatabase.localProductDao()
    }

    @AppScoped
    @Provides
    fun providesOrderDao(): OrderDao {
        return appDatabase.orderDao()
    }

    @AppScoped
    @Provides
    fun providesOrderItemDao(): OrderItemDao {
        return appDatabase.orderItemDao()
    }

    @AppScoped
    @Provides
    fun providesDistributorsDao(): DistributorsDao {
        return appDatabase.distributorsDao()
    }

    @AppScoped
    @Provides
    fun providesMasterVariantDao(): MasterVariantDao {
        return appDatabase.masterVariantDao()
    }

    @AppScoped
    @Provides
    fun providesLocalVariantDao(): LocalVariantDao {
        return appDatabase.localVariantDao()
    }
    @AppScoped
    @Provides
    fun providesPoDao(): PurchaseOrderDao {
        return appDatabase.purchaseOrderDao()
    }

    @AppScoped
    @Provides
    fun providesCreditHistoryDao(): CreditHistoryDao {
        return appDatabase.CreditHistoryDao()
    }

    @AppScoped
    @Provides
    internal fun providesMasterProductRepository(masterProductDao: MasterProductDao): MasterProductRepository {
        return MasterProductRepositoryImpl(masterProductDao)
    }

    @AppScoped
    @Provides
    internal fun CreditHistoryRepositoryRepository(creditHistoryDao: CreditHistoryDao): CreditHistoryRepository {
        return CreditHistoryRepositoryImp(creditHistoryDao)
    }

    @AppScoped
    @Provides
    internal fun providesLocalProductRepository(localProductDao: LocalProductDao): LocalProductRepository {
        return LocalProductRepositoryImpl(localProductDao)
    }

    @AppScoped
    @Provides
    internal fun providesOrderItemRepository(orderItemDao: OrderItemDao): OrderItemRepository {
        return OrderItemRepositoryImp(orderItemDao)
    }

    @AppScoped
    @Provides
    internal fun providesOrderRepository(orderDao: OrderDao): OrderRepository {
        return OrderRepositoryImp(orderDao)
    }

    @AppScoped
    @Provides
    internal fun providesMasterVariantRepository(masterVariantDao: MasterVariantDao): MasterVariantRepository {
        return MasterVariantRepositoryImpl(masterVariantDao)
    }


    @AppScoped
    @Provides
    internal fun providesLocalVariantRepository(localVariantDao: LocalVariantDao): LocalVariantRepository {
        return LocalVariantRepositoryImpl(localVariantDao)
    }

    @AppScoped
    @Provides
    internal fun providesCustomerRepository(customertDao: LocalCustomerDao): CustomerRepository {
        return CustomerRepositoryImp(customertDao)
    }

    @AppScoped
    @Provides
    internal fun providesUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImp(userDao)
    }

    @AppScoped
    @Provides
    internal fun providesDistributorsRepository(distributorsDao: DistributorsDao): DistributorsRepository {
        return DistributorsRepositoryImp(distributorsDao)
    }

    @AppScoped
    @Provides
    internal fun providesPurchaseOrderRepository(pODao: PurchaseOrderDao): PurchaseOrderRepository {
        return PurchaseOrderRepositoryImp(pODao)
    }



    @AppScoped
    @Provides
    internal fun provideDaggerWorkFactory(
        masterProductRepository: MasterProductRepository,
        masterVariantRepository: MasterVariantRepository,
        localProductRepository : LocalProductRepository,
        myServices: MyServices
    ): WorkerFactory {
        return DaggerWorkerFactory(
            masterProductRepository,
            masterVariantRepository,
            localProductRepository,
            myServices
        )
    }
}