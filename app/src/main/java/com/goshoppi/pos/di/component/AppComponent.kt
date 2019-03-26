package com.goshoppi.pos.di.component

import android.app.Application
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.architecture.workmanager.StoreProductImageWorker
import com.goshoppi.pos.architecture.workmanager.StoreVariantImageWorker
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.view.PosMainActivity
import com.goshoppi.pos.view.inventory.InventoryHomeActivity
import com.goshoppi.pos.view.inventory.InventoryProductDetailsActivity
import com.goshoppi.pos.view.inventory.LocalInventoryActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, RoomModule::class])
interface AppComponent {

    fun injectPosMainActivity(mainActivity: PosMainActivity)

    fun injectInventoryHomeActivity(inventoryHomeActivity: InventoryHomeActivity)

    fun injectInventoryProductDetailsActivity(inventoryProductDetailsActivity: InventoryProductDetailsActivity)

    fun injectLocalInventoryActivity(localInventoryActivity: LocalInventoryActivity)

    fun injectSyncWorker(syncWorker: SyncWorker)

    fun injectStoreVariantImageWorker( storeVariantImageWorker: StoreVariantImageWorker)

    fun injectStoreProductImageWorker( storeProductImageWorker: StoreProductImageWorker)

    fun getDatabase(): AppDatabase

    fun getLocalProductRepository(): LocalProductRepository

    fun getMasterProductRepository(): MasterProductRepository

    fun getLocalVariantRepository(): LocalVariantRepository

    fun getMasterVariantRepository(): MasterVariantRepository

    fun application(): Application

}