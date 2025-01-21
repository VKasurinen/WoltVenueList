package com.vkasurinen.woltmobile.data.remote

import WoltResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WoltApi {
    @GET("v1/pages/restaurants")
    suspend fun getVenues(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): WoltResponse
}