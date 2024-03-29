package com.goshoppi.pos.architecture

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goshoppi.pos.architecture.dao.*
import com.goshoppi.pos.architecture.helper.HelperConverter
import com.goshoppi.pos.model.*
import com.goshoppi.pos.model.local.*
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.model.master.ReceiveOrderItem


@Database(
    entities = [MasterProduct::class, MasterVariant::class,
        LocalProduct::class,
        LocalVariant::class,
        LocalCustomer::class,
        User::class,
        OrderItem::class,
        Order::class,
        CreditHistory::class,
        Distributor::class,
        ReceiveOrderItem::class,
        PurchaseOrderDetails::class,
        PurchaseOrder::class,
        PoHistory::class,
        AdminData::class,
        SubCategory::class,
        StoreCategory::class
    ],
    version = 2, exportSchema = false
)
@TypeConverters(HelperConverter::class,Converters::class)
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
    abstract fun purchaseOrderDao(): PurchaseOrderDao


}
