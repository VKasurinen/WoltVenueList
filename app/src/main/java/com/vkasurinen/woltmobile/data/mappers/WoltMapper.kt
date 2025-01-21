package com.vkasurinen.woltmobile.data.mappers

import com.vkasurinen.woltmobile.data.local.WoltEntity
import com.vkasurinen.woltmobile.data.remote.Venue
import com.vkasurinen.woltmobile.domain.model.WoltModel

fun WoltEntity.toWoltModel(): WoltModel {
    return WoltModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        isFavorite = isFavorite
    )
}


fun Venue.toWoltEntity(): WoltEntity {
    return WoltEntity(
        id = content_id ?: "",
        name = title ?: "",
        imageUrl = image?.url ?: "",
        description = description ?: ""
    )
}