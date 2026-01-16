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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.components.NoteItem
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import java.util.Date

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onNoteClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val allNotes by notesViewModel.allNotes.collectAsState()

    FavoritesContent(
        modifier = modifier,
        notes = allNotes,
        onNoteClick = onNoteClick
    )
}

@Composable
fun FavoritesContent(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onNoteClick: (Int) -> Unit
) {
    val favoriteNotes = remember(notes) {
        notes.filter { it.isPinned }
    }

    Surface() {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    top = MaterialTheme.dimens.paddingLarge,
                    bottom = MaterialTheme.dimens.paddingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.listSpacing)
            ) {
                items(items = favoriteNotes, key = { note -> note.id }) { note ->
                    val formattedDate = remember(note.lastEditDate) {
                        dateFormatterRelative(note.lastEditDate.time)
                    }
                    Box(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.paddingLarge)) {
                        NoteItem(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            date = formattedDate,
                            category = note.category,
                            onClick = { onNoteClick(note.id) },
                            isPinned = note.isPinned,
                            tags = note.tags
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoritesScreenPreview() {
    val mockNotes = listOf(
        Note(id = 1, title = "Pinned Note 1", content = "This is a pinned note.", tags = emptyList(), category = "Preview", isPinned = true, creationDate = Date(), lastEditDate = Date()),
        Note(id = 2, title = "Normal Note", content = "This note is not pinned.", tags = emptyList(), category = "Preview", isPinned = false, creationDate = Date(), lastEditDate = Date()),
        Note(id = 3, title = "Pinned Note 2", content = "This is another pinned note.", tags = emptyList(), category = "Preview", isPinned = true, creationDate = Date(), lastEditDate = Date())
    )
    GlyphNotesTheme {
        FavoritesContent(
            notes = mockNotes,
            onNoteClick = {}
        )
    }
}
