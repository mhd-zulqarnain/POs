package com.goshoppi.pos.architecture.repository.distributorsRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.Distributor

interface DistributorsRepository {
    fun loadAllDistributor(): LiveData<List<Distributor>>
    suspend  fun loadAllStaticDistributor(): List<Distributor>
    suspend fun insertDistributor(Distributor: Distributor)
    fun insertDistributors(DistributorList: List<Distributor>)
    fun searchDistributors(param: String): LiveData<List<Distributor>>
    suspend fun searchLocalStaticDistributors(param: String): List<Distributor>
    suspend fun deleteDistributors(phoneId: Long)
    suspend fun getDistributorCredit(distributorId: String): LiveData<Double>
    suspend fun getDistributorStaticCredit(distributorId: String):Double
    suspend  fun updateCredit(distributorId: String,credit:Double,date:String)
    suspend   fun getTotalDebit(): LiveData<Double>

}