// WoltMapper.kt
package com.vkasurinen.woltmobile.data.mappers

import VenueItem
import com.vkasurinen.woltmobile.data.local.WoltEntity
import com.vkasurinen.woltmobile.domain.model.WoltModel

fun WoltEntity.toWoltModel(): WoltModel {
    return WoltModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        isFavorite = isFavorite,
        address = address
    )
}

fun VenueItem.toWoltEntity(): WoltEntity {
    return WoltEntity(
        id = venue?.id ?: "",
        name = venue?.name ?: "",
        imageUrl = image?.url ?: "",
        description = venue?.short_description ?: "",
        address = venue?.address ?: ""
    )
}