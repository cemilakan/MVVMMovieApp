package net.kisacasi.myarchive.db

import androidx.room.TypeConverter
import java.util.*

class DataConverter {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return if (date == null) {
            null
        } else date.getTime()
    }
}