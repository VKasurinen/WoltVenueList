package com.vkasurinen.woltmobile.presentation.venueList

import androidx.paging.PagingData
import com.vkasurinen.woltmobile.domain.model.WoltModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class VenueState(
    val venues: List<WoltModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)