import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vkasurinen.woltmobile.data.local.WoltDao
import com.vkasurinen.woltmobile.data.mappers.toWoltEntity
import com.vkasurinen.woltmobile.data.mappers.toWoltModel
import com.vkasurinen.woltmobile.data.remote.WoltApi
import com.vkasurinen.woltmobile.domain.model.WoltModel
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

            val venuesFromApi = try {
                Log.d("WoltRepo", "Getting venues from api $latitude, $longitude")
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

            val venueEntities = venuesFromApi.sections
                .flatMap { it.items }
                .filter { it.venue != null && !it.venue.name.isNullOrEmpty() }
                .map { it.toWoltEntity() }

            // Preserve the isFavorite status
            val existingVenues = dao.getAllVenues()
            val mergedVenues = venueEntities.map { newVenue ->
                val existingVenue = existingVenues.find { it.id == newVenue.id }
                if (existingVenue != null) {
                    newVenue.copy(isFavorite = existingVenue.isFavorite)
                } else {
                    newVenue
                }
            }

            dao.insertVenues(mergedVenues)

            emit(Resource.Success(
                data = mergedVenues.map { it.toWoltModel() }
            ))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getVenue(
        venueId: String
    ): Flow<Resource<WoltModel>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val venueEntity = dao.getVenue(venueId)
                if (venueEntity != null) {
                    emit(Resource.Success(data = venueEntity.toWoltModel()))
                } else {
                    emit(Resource.Error(message = "Venue not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(message = "Error loading venue: ${e.message}"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }

    override suspend fun getFavoriteVenues(): Flow<Resource<List<WoltModel>>> {
        return flow {
            try {
                emit(Resource.Loading(true))
                val favoriteVenues = dao.getFavoriteVenues()
                emit(Resource.Success(data = favoriteVenues.map { it.toWoltModel() }))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unknown error occurred"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }


}