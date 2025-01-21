package com.vkasurinen.woltmobile.domain.model

data class WoltModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val isFavorite: Boolean
)