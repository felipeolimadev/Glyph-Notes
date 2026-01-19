package com.felipeserver.site.glyphnotes.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen

class NoteDetailScreenLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlyphNotesTheme() {
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = Screen.NoteRichTextEditor.rout
                ) {
                    composable(
                        route = Screen.NoteRichTextEditor.rout, arguments = listOf(
                        navArgument("noteId") { type = NavType.IntType }
                    )) { backStackEntry ->
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
