package com.goshoppi.pos.architecture

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.goshoppi.pos.architecture.helper.HelperConverter
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.model.Variant


@Database(entities = [Product::class, Variant::class], version = 29, exportSchema = false)
@TypeConverters(HelperConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        private val LOCK = Any()
        private val DATABASE_NAME = "moviedb"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val factory = SafeHelperFactory.fromUser(SpannableStringBuilder("sekrit"));

            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java,
                        DATABASE_NAME
                    )
                            .openHelperFactory(factory)
                            .build()
                }
            }
            return sInstance!!
        }
    }
}
