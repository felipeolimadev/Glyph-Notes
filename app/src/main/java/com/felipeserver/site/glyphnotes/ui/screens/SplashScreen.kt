package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplashScreen(navController: NavController, usernameState: String?) {

    LaunchedEffect(usernameState) {
        // Se usernameState for não-nulo, a lógica no MainActivity já deve ter navegado, mas se não, navegamos aqui.
        if (usernameState != null) {
            val route = if (usernameState.isBlank()) {
                Screen.OnBoarding.rout
            } else {
                Screen.Home.rout
            }
            navController.navigate(route) {
                popUpTo(Screen.SplashScreen.rout) {
                    inclusive = true
                }
            }
        } else {
            // Se usernameState é null (carregando), esperamos um pouco e forçamos para OnBoarding
            // como um fallback, caso o DataStore esteja vazio/lento.
            delay(2000L) 
            if (usernameState == null) {
                 navController.navigate(Screen.OnBoarding.rout) {
                    popUpTo(Screen.SplashScreen.rout) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Scaffold() { innerPadding ->
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0f0c29),
                            Color(0xFF302b63),
                            Color(0xFF24243e)
                        ),
                        start = Offset(0f, 0f), // Topo esquerdo
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(500.dp),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Splash Screen"
                )
                Text(
                    text = "Glyph Notes",
                    style = MaterialTheme.typography.displayLargeEmphasized
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SplashScreenPreview() {
    GlyphNotesTheme {
        SplashScreen(navController = NavController(LocalContext.current), usernameState = null)
    }
}