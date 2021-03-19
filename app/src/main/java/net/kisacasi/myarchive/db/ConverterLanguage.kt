package net.kisacasi.myarchive.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.kisacasi.myarchive.models.SpokenLanguage
import java.lang.reflect.Type
import java.util.*


class ConverterLanguage {
    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<SpokenLanguage?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type =
            object : TypeToken<List<SpokenLanguage?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<SpokenLanguage?>?): String? {
        return gson.toJson(someObjects)
    }
}