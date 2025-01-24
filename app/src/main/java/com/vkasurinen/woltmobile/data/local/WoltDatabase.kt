package com.vkasurinen.woltmobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WoltEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class WoltDatabase : RoomDatabase() {
    abstract fun woltDao(): WoltDao
}