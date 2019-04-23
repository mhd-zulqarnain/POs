package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.goshoppi.pos.model.local.LocalCustomer


@Dao
interface LocalCustomerDao {

    @Query("SELECT * FROM local_customers")
    fun loadLocalAllCustomer(): LiveData<List<LocalCustomer>>

    @Query("SELECT * FROM local_customers")
    fun loadLocalAllStaticCustomer(): List<LocalCustomer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalCustomer(customer: LocalCustomer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalCustomers(customers:  List<LocalCustomer>)

    @Query("DELETE FROM  local_customers WHERE  phone= :phoneId")
    fun deleteLocalCustomers(phoneId: Long)

    @Query("SELECT * FROM  local_customers WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<LocalCustomer>>

    @Query("SELECT * FROM  local_customers WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchStaticResult(dealText: String): List<LocalCustomer>
}
