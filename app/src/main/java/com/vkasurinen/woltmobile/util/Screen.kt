package com.vkasurinen.woltmobile.util

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Details : Screen("details")
}