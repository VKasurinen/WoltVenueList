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
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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
                    api.getVenues(latitude, longitude) // Attempt to fetch venues from the API
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

                val venueEntities = venuesFromApi.sections // Extract venue entities from the API response
                    .flatMap { it.items } // Flatten sections into a list of items
                    .filter { it.venue != null && !it.venue.name.isNullOrEmpty() } // Filter valid venues
                    .map { it.toWoltEntity() } // Map items to WoltEntity

                // Preserve the isFavorite status
                val existingVenues = dao.getAllVenues()
                val mergedVenues = venueEntities.map { newVenue ->  // Merge the new venues with existing ones, preserving the isFavorite status
                    val existingVenue = existingVenues.find { it.id == newVenue.id }
                    if (existingVenue != null) {
                        newVenue.copy(isFavorite = existingVenue.isFavorite) // If a match is found, copy the isFavorite status to the new venue
                    } else {
                        newVenue // Otherwise, use the new venue as is
                    }
                }

                dao.insertVenues(mergedVenues) // Insert the merged venues into the database

                emit(Resource.Success(
                    data = mergedVenues.map { it.toWoltModel() } // Emit a success state with the list of merged venues mapped to WoltModel
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
                val venueEntity = dao.getVenue(venueId) // Attempt to fetch the venue entity from the database using its ID
                if (venueEntity != null) {
                    emit(Resource.Success(data = venueEntity.toWoltModel()))  // If the venue exists, emit a success state with the venue converted to a WoltModel
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
                val favoriteVenues = dao.getFavoriteVenues() // Fetch the list of favorite venues from the database
                emit(Resource.Success(data = favoriteVenues.map { it.toWoltModel() })) // Convert the list of venue entities into WoltModel objects and emit as a success state
            } catch (e: Exception) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unknown error occurred"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }


}