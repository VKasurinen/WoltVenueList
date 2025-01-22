package com.vkasurinen.woltmobile

import WoltRepository
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vkasurinen.woltmobile.data.local.WoltDao
import com.vkasurinen.woltmobile.ui.theme.WoltMobileTheme
import com.vkasurinen.woltmobile.util.Screen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val woltListRepository: WoltRepository by inject()
    private val woltDao: WoltDao by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WoltMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Main.route
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(navController)
                        }
//                        composable(Screen.Details.route) {
//                            val isTopNews = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isTopNews") ?: false
//                            DetailsScreenRoot(navController = navController, isTopNews = isTopNews)
//                        }
                    }
                }
            }
        }
    }



    private fun checkDatabaseStatus() {
        lifecycleScope.launch {
            try {
                val allVenues = woltDao.getAllVenues()
                val favoriteVenues = woltDao.getFavoriteVenues()

                Log.d("DatabaseStatus", "All Venues: ${allVenues.map { it.id to it.isFavorite }}")
                Log.d("DatabaseStatus", "Favorite Venues: ${favoriteVenues.map { it.id }}")
            } catch (e: Exception) {
                Log.e("DatabaseStatus", "Error checking database status", e)
            }
        }
    }



//    private fun insertAndLogVenue() {
//        lifecycleScope.launch {
//            try {
//                // Create a new venue
//                val newVenue = WoltEntity(
//                    id = "new_id",
//                    name = "New Venue",
//                    imageUrl = "https://example.com/image.jpg",
//                    description = "This is a new venue.",
//                    address = "123 New Street",
//                    isFavorite = false
//                )
//
//                // Insert the new venue into the database
//                woltDao.insertVenues(listOf(newVenue))
//                Log.d("DatabaseStatus", "New venue inserted: $newVenue")
//
//                // Retrieve and log all venues
//                val allVenues = woltDao.getAllVenues()
//                Log.d("DatabaseStatus", "All Venues after insertion: ${allVenues.map { it.id to it.name }}")
//            } catch (e: Exception) {
//                Log.e("DatabaseStatus", "Error inserting and logging venue", e)
//            }
//        }
//    }




//    private fun testWoltListRepository() {
//        CoroutineScope(Dispatchers.IO).launch {
//            woltListRepository.getVenues(60.1699, 24.9384, 15, 0, false).collect { resource ->
//                when (resource) {
//                    is Resource.Loading -> {
//                        Log.d("MainActivity", "Loading data...")
//                    }
//
//                    is Resource.Success -> {
//                        Log.d("MainActivity", "Data loaded successfully: ${resource.data}")
//                    }
//
//                    is Resource.Error -> {
//                        Log.e("MainActivity", "Error loading data: ${resource.message}")
//                    }
//                }
//            }
//        }
//    }
}
