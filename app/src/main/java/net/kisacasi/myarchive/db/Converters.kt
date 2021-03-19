package net.kisacasi.myarchive.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.kisacasi.myarchive.models.Genre
import java.lang.reflect.Type
import java.util.*


class Converters {
    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Genre?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type =
            object : TypeToken<List<Genre?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Genre?>?): String? {
        return gson.toJson(someObjects)
    }
}