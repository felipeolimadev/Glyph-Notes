package com.felipeserver.site.glyphnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.felipeserver.site.glyphnotes.ui.components.RichTextEditorNoteDetail
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            @OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                        Screen.Favorites.rout
                    )

                    Scaffold(
                        floatingActionButton = {
                            if (showBottomBar) {
                                HorizontalFloatingToolbar(
                                    expanded = true,
                                    floatingActionButton = {
                                        FloatingActionButton(
                                            onClick = { navController.navigate("note_rich_text_editor_screen/-1") },
                                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                        ) {
                                            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
                                        }
                                    },
                                    content = {
                                        val items = listOf(
                                            Screen.Home to Icons.Default.Home,
                                            Screen.Favorites to Icons.Default.Favorite
                                        )
                                        items.forEach { (screen, icon) ->
                                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.rout } == true
                                            IconButton(
                                                onClick = {
                                                    val route = screen.rout.replace("{noteId}", "-1")
                                                    navController.navigate(route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                colors = if (isSelected) {
                                                    androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                } else {
                                                    androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
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
                                    modifier = Modifier,
                                    contentPadding = innerPadding,
                                    onNoteClick = { noteId ->
                                        navController.navigate("note_rich_text_editor_screen/$noteId")
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
                                        navController.navigate("note_rich_text_editor_screen/$noteId")
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
                            composable(
                                route = Screen.NoteRichTextEditor.rout,
                                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                                RichTextEditorNoteDetail(
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GlyphNotesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val startDestination = Screen.Home.rout

            // Simulation of back stack for preview
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            // Force showBottomBar to true for preview or check if current destination is null (initial state)
            val showBottomBar = currentDestination?.route in listOf(
                Screen.Home.rout,
                Screen.Favorites.rout
            ) || currentDestination == null

            Scaffold(
                floatingActionButton = {
                    if (showBottomBar) {
                        HorizontalFloatingToolbar(
                            expanded = true,
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
                                }
                            },
                            content = {
                                val items = listOf(
                                    Screen.Home to Icons.Default.Home,
                                    Screen.Favorites to Icons.Default.Favorite
                                )
                                items.forEach { (screen, icon) ->
                                    val isSelected = screen == Screen.Home // Force Home selected for preview
                                    IconButton(
                                        onClick = { },
                                        colors = if (isSelected) {
                                            androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        } else {
                                            androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.Center
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable(Screen.Home.rout) {
                        val calendar = java.util.Calendar.getInstance()
                        val today = calendar.time
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
                        val yesterday = calendar.time
                        
                        val mockNotes = listOf(
                            com.felipeserver.site.glyphnotes.data.db.Note(
                                id = 1,
                                title = "Ideias para o App",
                                content = "Explorar novas features de IA para o editor de notas.",
                                tags = listOf("IA", "Dev", "Mobile"),
                                category = "Trabalho",
                                isPinned = true,
                                creationDate = today,
                                lastEditDate = today
                            ),
                            com.felipeserver.site.glyphnotes.data.db.Note(
                                id = 2,
                                title = "Lista de Compras",
                                content = "Leite, pão, ovos e café.",
                                tags = listOf("Casa", "Mercado"),
                                category = "Pessoal",
                                isPinned = false,
                                creationDate = today,
                                lastEditDate = today
                            ),
                            com.felipeserver.site.glyphnotes.data.db.Note(
                                id = 3,
                                title = "Resumo da Reunião",
                                content = "Enviar o resumo sobre o alinhamento do projeto.",
                                tags = emptyList(),
                                category = "Trabalho",
                                isPinned = false,
                                creationDate = yesterday,
                                lastEditDate = yesterday
                            )
                        )
                        
                        com.felipeserver.site.glyphnotes.ui.screens.HomeContent(
                            modifier = Modifier,
                            contentPadding = innerPadding,
                            notes = mockNotes,
                            onNoteClick = { },
                            onNoteDelete = { },
                            searchQuery = "",
                            onQueryChange = { },
                            selectedTags = emptySet(),
                            onTagsChanged = { },
                            showFilterSheet = false,
                            onFilterSheetChange = { }
                        )
                    }
                }
            }
        }
    }
}
