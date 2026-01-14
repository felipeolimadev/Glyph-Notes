package com.felipeserver.site.glyphnotes.ui.viewmodel.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
    ) {
    companion object {
        val navigationItems = listOf(
            NavigationItem(
                "Home",
                Icons.Default.Home,
                Screen.Home.rout
            ),
            NavigationItem(
                "Favorite",
                Icons.Default.Favorite,
                Screen.Favorites.rout
            ),
            NavigationItem(
                "Calendar",
                Icons.Default.CalendarToday,
                Screen.Calendar.rout
            ),
            NavigationItem(
                "Settings",
                Icons.Default.Settings,
                Screen.Settings.rout
            )
        )
    }
}
