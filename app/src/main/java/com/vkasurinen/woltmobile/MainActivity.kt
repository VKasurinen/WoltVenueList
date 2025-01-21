package com.vkasurinen.woltmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vkasurinen.woltmobile.domain.repository.WoltRepository
import com.vkasurinen.woltmobile.ui.theme.WoltMobileTheme
import com.vkasurinen.woltmobile.util.Resource
import com.vkasurinen.woltmobile.util.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val woltListRepository: WoltRepository by inject()

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
        testWoltListRepository()
    }

    private fun testWoltListRepository() {
        CoroutineScope(Dispatchers.IO).launch {
            woltListRepository.getVenues(60.1699, 24.9384, false).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("MainActivity", "Loading data...")
                    }

                    is Resource.Success -> {
                        Log.d("MainActivity", "Data loaded successfully: ${resource.data}")
                    }

                    is Resource.Error -> {
                        Log.e("MainActivity", "Error loading data: ${resource.message}")
                    }
                }
            }
        }
    }
}
