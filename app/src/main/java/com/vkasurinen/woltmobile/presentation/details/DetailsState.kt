package com.vkasurinen.woltmobile.presentation.details

import com.vkasurinen.woltmobile.domain.model.WoltModel

data class DetailsState (
    val venue: WoltModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)





