package com.felipeserver.site.glyphnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.felipeserver.site.glyphnotes.ui.screens.CalendarScreen
import com.felipeserver.site.glyphnotes.ui.screens.FavoritesScreen
import com.felipeserver.site.glyphnotes.ui.screens.HomeScreen
import com.felipeserver.site.glyphnotes.ui.screens.NoteDetailScreen
import com.felipeserver.site.glyphnotes.ui.screens.OnBoardingUserName
import com.felipeserver.site.glyphnotes.ui.screens.SettingsScreen
import com.felipeserver.site.glyphnotes.ui.screens.SplashScreen
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.getUsername
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale.getDefault

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlyphNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val userNameState by getUsername(context).collectAsState(initial = null)

                    var isDataLoaded by remember { mutableStateOf(userNameState != null) }

                    LaunchedEffect(userNameState) {
                        if (userNameState != null) {
                            isDataLoaded = true
                        }
                    }

                    val startDestination = when {
                        !isDataLoaded -> Screen.SplashScreen.rout
                        userNameState.isNullOrBlank() -> Screen.OnBoarding.rout
                        else -> Screen.Home.rout
                    }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val showBottomBar = currentDestination?.route in listOf(
                        Screen.Home.rout,
                        Screen.Favorites.rout,
                        Screen.Calendar.rout
                    )

                    Scaffold(
                        floatingActionButton = {
                            if (showBottomBar) {
                                FloatingActionButton(
                                    onClick = { navController.navigate("note_detail_screen/-1") },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
                                }
                            }
                        },
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar {
                                    val items = listOf(
                                        Screen.Home to Icons.Default.Home,
                                        Screen.Favorites to Icons.Default.Favorite,
                                        Screen.Calendar to Icons.Default.CalendarMonth
                                    )
                                    items.forEach { (screen, icon) ->
                                        NavigationBarItem(
                                            icon = { Icon(icon, contentDescription = null) },
                                            label = { Text(
                                                screen.rout.substringBefore("_")
                                                    .replaceFirstChar {
                                                        if (it.isLowerCase()) it.titlecase(
                                                            getDefault()
                                                        ) else it.toString()
                                                    }) },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.rout } == true,
                                            onClick = {
                                                navController.navigate(screen.rout) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                        ) {
                            composable(Screen.SplashScreen.rout) {
                                if (isDataLoaded) {
                                    LaunchedEffect(Unit) {
                                        val route = if (userNameState.isNullOrBlank()) Screen.OnBoarding.rout else Screen.Home.rout
                                        navController.navigate(route) {
                                            popUpTo(Screen.SplashScreen.rout) { inclusive = true }
                                        }
                                    }
                                }
                                SplashScreen(navController, userNameState)
                            }
                            composable(Screen.OnBoarding.rout) {
                                OnBoardingUserName(navController)
                            }
                            composable(Screen.Home.rout) {
                                HomeScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNoteClick = { noteId ->
                                        navController.navigate("note_detail_screen/$noteId")
                                    }
                                )
                            }
                            composable(Screen.Settings.rout) {
                                SettingsScreen(modifier = Modifier.padding(innerPadding))
                            }
                            composable(Screen.Favorites.rout) {
                                FavoritesScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNoteClick = { noteId ->
                                        navController.navigate("note_detail_screen/$noteId")
                                    }
                                )
                            }
                            composable(Screen.Calendar.rout) {
                                CalendarScreen(modifier = Modifier.padding(innerPadding))
                            }
                            composable(
                                route = Screen.NoteDetail.rout,
                                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                                NoteDetailScreen(
                                    id = noteId,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}