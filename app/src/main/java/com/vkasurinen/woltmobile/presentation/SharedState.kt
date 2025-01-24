package com.vkasurinen.woltmobile.presentation

import com.vkasurinen.woltmobile.domain.model.WoltModel

data class SharedState(
    val venues: List<WoltModel> = emptyList(),
    val venue: WoltModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)