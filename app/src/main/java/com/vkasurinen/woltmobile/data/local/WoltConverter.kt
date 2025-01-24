package com.vkasurinen.woltmobile.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class Converters {
    @TypeConverter
    fun fromVenuePreviewItemList(value: List<VenuePreviewItemEntity>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<VenuePreviewItemEntity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVenuePreviewItemList(value: String): List<VenuePreviewItemEntity>? {
        val gson = Gson()
        val type = object : TypeToken<List<VenuePreviewItemEntity>>() {}.type
        return gson.fromJson(value, type)
    }
}