package com.vkasurinen.woltmobile.di

import androidx.room.Room
import com.vkasurinen.woltmobile.data.local.WoltDatabase
import com.vkasurinen.woltmobile.data.remote.WoltApi
import com.vkasurinen.woltmobile.data.repository.WoltRepositoryImpl
import com.vkasurinen.woltmobile.domain.repository.WoltRepository
import com.vkasurinen.woltmobile.presentation.venueList.VenueViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurant-api.wolt.com/")
            .client(get())
            .build()
            .create(WoltApi::class.java)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            WoltDatabase::class.java,
            "woltdb.db"
        ).build()
    }

    single<WoltRepository> { WoltRepositoryImpl(get(), get<WoltDatabase>().woltDao()) }

    viewModel { VenueViewModel(get())}

}