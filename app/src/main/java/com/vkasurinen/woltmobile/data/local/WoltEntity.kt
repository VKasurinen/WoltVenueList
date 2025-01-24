package com.vkasurinen.woltmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "venues")
data class WoltEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val address: String,
    val isFavorite: Boolean = false,
    val rating: Double?,
    val shortDescription: String?,
    val venuePreviewItems: List<VenuePreviewItemEntity>?
)

data class VenuePreviewItemEntity(
    val id: String?,
    val name: String?,
    val price: Int?,
    val imageUrl: String?,
    val currency: String?
)