package net.kisacasi.myarchive.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.kisacasi.myarchive.models.ProductionCompany
import java.lang.reflect.Type
import java.util.*


class ConverterCompany {
    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<ProductionCompany?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type =
            object : TypeToken<List<ProductionCompany?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<ProductionCompany?>?): String? {
        return gson.toJson(someObjects)
    }
}