package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
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
    fun insertLocalCustomers(customers: List<LocalCustomer>)

    @Query("DELETE FROM  local_customers WHERE  phone= :phoneId")
    fun deleteLocalCustomers(phoneId: Long)

    @Query("SELECT * FROM  local_customers WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<LocalCustomer>>

    @Query("SELECT * FROM  local_customers WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchStaticResult(dealText: String): List<LocalCustomer>

    @Query("SELECT COUNT(*) FROM orders WHERE customerId=:customerId ")
    fun getTotalOrder(customerId: String): LiveData<Int>

    @Query("SELECT SUM(orderAmount) FROM orders WHERE customerId=:customerId ")
    fun getTotalTransaction(customerId: String): LiveData<Int>

    @Query("SELECT * FROM orders WHERE customerId=:customerId ")
    fun getListOfOrders(customerId: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE customerId=:customerId AND  (orderDate BETWEEN :upperLimit AND :lowerLimit) ")
    fun filterListOfOrdersByRange(customerId: String,
                                  upperLimit:String ,
                                  lowerLimit:String ): LiveData<List<Order>>

    @Query("SELECT * FROM order_item WHERE orderId=:orderId ")
    fun getListOfOrderItem(orderId: String): LiveData<List<OrderItem>>

    @Query("SELECT totalCredit FROM local_customers WHERE phone=:customerId ")
    fun getCustomerCredit(customerId: String): LiveData<Double>

    @Query("SELECT totalCredit FROM local_customers WHERE phone=:customerId ")
    fun getCustomerStaticCredit(customerId: String): Double

    @Query("Update local_customers set totalCredit=:credit ,updatedAt =:date where phone=:customerId")
    fun updateCredit(customerId: String,credit:Double,date:String)

    @Query("SELECT SUM(totalCredit) FROM local_customers ")
    fun getTotalDebit(): LiveData<Double>

}
