package com.felipeserver.site.glyphnotes.ui.viewmodel.navigation

sealed class Screen(val rout: String) {

    object Home: Screen("home_screen")
    object Folder: Screen("folder_screen")
    object Calendar: Screen("calendar_screen")
    object Settings: Screen("setting_screen")


}