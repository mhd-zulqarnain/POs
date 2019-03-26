package com.goshoppi.pos.architecture

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.architecture.dao.LocalVariantDao
import com.goshoppi.pos.architecture.dao.MasterVariantDao
import com.goshoppi.pos.architecture.helper.HelperConverter
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant

@Database(
    entities = [MasterProduct::class, MasterVariant::class,
        LocalProduct::class, LocalVariant::class], version = 1, exportSchema = false
)
@TypeConverters(HelperConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localProductDao(): LocalProductDao
    abstract fun masterProductDao(): MasterProductDao
    abstract fun masterVariantDao(): MasterVariantDao
    abstract fun localVariantDao(): LocalVariantDao

}
