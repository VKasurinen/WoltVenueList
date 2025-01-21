import androidx.paging.PagingSource
import com.vkasurinen.woltmobile.domain.model.WoltModel
import com.vkasurinen.woltmobile.util.Resource
import kotlinx.coroutines.flow.Flow

interface WoltRepository {
    suspend fun getVenues(latitude: Double, longitude: Double, forceFetchFromRemote: Boolean): Flow<Resource<List<WoltModel>>>
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    suspend fun getFavoriteVenues(): Flow<Resource<List<WoltModel>>>
}
