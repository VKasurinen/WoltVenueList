package com.vkasurinen.woltmobile.presentation.favorites

import com.vkasurinen.woltmobile.domain.model.WoltModel

data class FavoriteState(
    val favoriteVenues: List<WoltModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
