package net.kisacasi.myarchive.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.kisacasi.myarchive.models.ProductionCountry
import java.lang.reflect.Type
import java.util.*


class ConverterCountry {
    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<ProductionCountry?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type =
            object : TypeToken<List<ProductionCountry?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<ProductionCountry?>?): String? {
        return gson.toJson(someObjects)
    }
}