package com.goshoppi.pos.architecture

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.goshoppi.pos.architecture.dao.ProductDao
import com.goshoppi.pos.architecture.dao.VariantDao
import com.goshoppi.pos.architecture.helper.HelperConverter
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant

@Database(entities = [MasterProduct::class, MasterVariant::class,
    LocalProduct::class, LocalVariant::class], version = 1, exportSchema = false)
@TypeConverters(HelperConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun varaintDao(): VariantDao

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "GoShoppiPosDb"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
//            val factory = SafeHelperFactory.fromUser(SpannableStringBuilder("encryptDb"))

            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java,
                        DATABASE_NAME
                    )
//                            .openHelperFactory(factory)
                        .allowMainThreadQueries().build()
                }
            }
            return sInstance!!
        }
    }
}
