package com.felipeserver.site.glyphnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipeserver.site.glyphnotes.ui.screens.HomeScreen
import com.felipeserver.site.glyphnotes.ui.screens.OnBoardingUserName
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
            GlyphNotesTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val userNameState by getUsername(context).collectAsState(initial = null)

                    // Rastreia se o estado inicial (null) mudou para um valor definido
                    var isDataLoaded by remember { mutableStateOf(userNameState != null) }

                    LaunchedEffect(userNameState) {
                        if (userNameState != null) {
                            isDataLoaded = true
                        }
                    }

                    // Define a rota inicial com base no estado de carregamento
                    val startDestination = when {
                        !isDataLoaded -> Screen.SplashScreen.rout
                        userNameState.isNullOrBlank() -> Screen.OnBoarding.rout
                        else -> Screen.Home.rout
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable(Screen.SplashScreen.rout) {
                            // Se isDataLoaded for true, significa que o startDestination deveria ter sido outro.
                            // Se o NavHost ainda está aqui, forçamos a navegação imediatamente.
                            if (isDataLoaded) {
                                LaunchedEffect(Unit) {
                                    val route = if (userNameState.isNullOrBlank()) Screen.OnBoarding.rout else Screen.Home.rout
                                    navController.navigate(route) {
                                        popUpTo(Screen.SplashScreen.rout) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                            // O userNameState pode ser null aqui se isDataLoaded for false (tela de carregamento)
                            SplashScreen(navController, userNameState)
                        }
                        composable(Screen.OnBoarding.rout) {
                            OnBoardingUserName(navController)
                        }
                        composable(Screen.Home.rout) {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}