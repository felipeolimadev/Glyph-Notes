package com.felipeserver.site.glyphnotes.ui.screens


import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.ui.components.NoteItem
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import java.util.Date

/**
 *
 */
@Composable
fun FavoritesScreen(notes: List<Note>, onNoteClick: (Int) -> Unit, paddingValues: PaddingValues) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(MaterialTheme.dimens.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.listSpacing)
            ) {
                items(items = notes.filter { it.isPinned }, key = { note -> note.id }) { note ->
                    val formattedDate = remember(note.lastEditDate) {
                        dateFormatterRelative(note.lastEditDate.time)
                    }
                    NoteItem(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        date = formattedDate,
                        category = note.category,
                        onClick = { onNoteClick(note.id) })

                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoritesScreenPreview() {
    val mockNotes = listOf(
        Note(
            id = 1,
            title = "Preview Note",
            content = "Esta é uma nota de exemplo para o preview.",
            tags = emptyList(),
            category = "Preview",
            isPinned = false,
            creationDate = Date(),
            lastEditDate = Date()
        )
    )
    GlyphNotesTheme() {
        FavoritesScreen(
            notes = mockNotes,
            onNoteClick = {},
            paddingValues = PaddingValues()
        )
    }

}