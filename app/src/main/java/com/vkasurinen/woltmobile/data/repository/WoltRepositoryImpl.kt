package com.vkasurinen.woltmobile.data.repository

import com.vkasurinen.woltmobile.data.local.WoltDao
import com.vkasurinen.woltmobile.data.mappers.toWoltEntity
import com.vkasurinen.woltmobile.data.mappers.toWoltModel
import com.vkasurinen.woltmobile.data.remote.WoltApi
import com.vkasurinen.woltmobile.domain.model.WoltModel
import com.vkasurinen.woltmobile.domain.repository.WoltRepository
import com.vkasurinen.woltmobile.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException


class WoltRepositoryImpl(
    private val api: WoltApi,
    private val dao: WoltDao
) : WoltRepository {

    override suspend fun getVenues(latitude: Double, longitude: Double, forceFetchFromRemote: Boolean): Flow<Resource<List<WoltModel>>> {
        return flow {
            emit(Resource.Loading(true))
            val localVenues = dao.getAllVenues()

            val shouldLoadLocalVenues = localVenues.isNotEmpty() && !forceFetchFromRemote

            if (shouldLoadLocalVenues) {
                emit(Resource.Success(
                    data = localVenues.map { it.toWoltModel() }
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val venuesFromApi = try {
                api.getVenues(latitude, longitude)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading venues"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading venues"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading venues"))
                return@flow
            }

            val venueEntities = venuesFromApi.sections.flatMap { it.items }.map { it.toWoltEntity() }

            dao.insertVenues(venueEntities)

            emit(Resource.Success(
                data = venueEntities.map { it.toWoltModel() }
            ))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }
}