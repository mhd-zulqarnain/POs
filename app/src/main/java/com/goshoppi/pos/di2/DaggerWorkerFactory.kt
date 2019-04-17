package com.goshoppi.pos.di2

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.architecture.workmanager.StoreProductImageWorker
import com.goshoppi.pos.architecture.workmanager.StoreVariantImageWorker
import com.goshoppi.pos.architecture.workmanager.SyncWorker
import com.goshoppi.pos.webservice.retrofit.MyServices

class DaggerWorkerFactory(private var masterProductRepository : MasterProductRepository,
                          private var masterVariantRepository : MasterVariantRepository,
                          private var myServices: MyServices

) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {

        val workerKlass = Class.forName(workerClassName).asSubclass(Worker::class.java)
        val constructor = workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is SyncWorker -> {
                instance.masterProductRepository = masterProductRepository
                instance.masterVariantRepository = masterVariantRepository
                instance.myServices = myServices
            }
            is StoreVariantImageWorker -> {
                instance.masterProductRepository = masterProductRepository
                instance.masterVariantRepository = masterVariantRepository
            }
            is StoreProductImageWorker -> {
                instance.masterProductRepository = masterProductRepository
            }
        }

        return instance
    }
}