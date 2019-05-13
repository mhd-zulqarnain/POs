package com.goshoppi.pos.architecture

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goshoppi.pos.architecture.dao.*
import com.goshoppi.pos.architecture.helper.HelperConverter
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.User
import com.goshoppi.pos.model.local.*
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant


@Database(
    entities = [MasterProduct::class, MasterVariant::class,
        LocalProduct::class,
        LocalVariant::class,
        LocalCustomer::class,
        User::class,
        OrderItem::class,
        Order::class,
        CreditHistory::class,
        Distributor::class

    ],
    version = 3, exportSchema = false
)
@TypeConverters(HelperConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localProductDao(): LocalProductDao
    abstract fun masterProductDao(): MasterProductDao
    abstract fun masterVariantDao(): MasterVariantDao
    abstract fun localVariantDao(): LocalVariantDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun localCustomerDao(): LocalCustomerDao
    abstract fun UserDao(): UserDao
    abstract fun CreditHistoryDao(): CreditHistoryDao
    abstract fun distributorsDao(): DistributorsDao


}
