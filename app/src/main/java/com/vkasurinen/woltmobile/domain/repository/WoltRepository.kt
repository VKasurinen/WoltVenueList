package com.vkasurinen.woltmobile.domain.repository

import com.vkasurinen.woltmobile.data.local.WoltEntity
import com.vkasurinen.woltmobile.domain.model.WoltModel
import com.vkasurinen.woltmobile.util.Resource
import kotlinx.coroutines.flow.Flow

interface WoltRepository {
    suspend fun getVenues(latitude: Double, longitude: Double, forceFetchFromRemote: Boolean): Flow<Resource<List<WoltModel>>>
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
}