package com.goshoppi.pos.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.ENGLISH


@Entity(
    tableName = "credit_history"
)
class CreditHistory {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var customerId: Long? = null
    var orderId: Long? = null
    var creditAmount: Double ? = null
    var paidAmount: Double ? = null
    var totalCreditAmount: Double ? = null
    var transcationDate :Date ? = null

}
class Converters {
    internal var df: DateFormat = SimpleDateFormat("MM/dd/yyyy",Locale.getDefault())

   /* @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }*/

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?):Date? {
        if (value != null) {
            try {
                val time =  SimpleDateFormat("MM/dd/yyyy",Locale.getDefault()).format(value)
                Timber.e("SimpleDateFormat : ${df.parse(time) as Date}")
                return df.parse(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        } else {
            return null
        }
    }
}
