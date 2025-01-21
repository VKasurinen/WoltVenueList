package com.vkasurinen.woltmobile.presentation.venueList

import com.vkasurinen.woltmobile.domain.model.WoltModel

data class VenueState(
    val venues: List<WoltModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)