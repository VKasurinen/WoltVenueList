package com.vkasurinen.woltmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vkasurinen.woltmobile.domain.repository.WoltRepository
import com.vkasurinen.woltmobile.ui.theme.WoltMobileTheme
import com.vkasurinen.woltmobile.util.Resource
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WoltMobileTheme {
        Greeting("Android")
    }
}