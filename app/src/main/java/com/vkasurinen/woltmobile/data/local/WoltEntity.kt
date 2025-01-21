package com.vkasurinen.woltmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "venues")
data class WoltEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val isFavorite: Boolean = false
)