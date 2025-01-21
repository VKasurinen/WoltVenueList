package com.vkasurinen.woltmobile.data.local

import androidx.room.*

@Dao
interface WoltDao {
    @Query("SELECT * FROM venues")
    suspend fun getAllVenues(): List<WoltEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenues(venues: List<WoltEntity>)

    @Query("UPDATE venues SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM venues WHERE isFavorite = 1")
    suspend fun getFavoriteVenues(): List<WoltEntity>
}