package com.vkasurinen.woltmobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WoltEntity::class], version = 1)
abstract class WoltDatabase : RoomDatabase() {
    abstract fun woltDao(): WoltDao
}