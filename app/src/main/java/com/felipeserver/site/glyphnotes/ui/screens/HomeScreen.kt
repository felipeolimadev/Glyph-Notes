package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.components.NoteItem
import com.felipeserver.site.glyphnotes.ui.components.ProfileBar
import com.felipeserver.site.glyphnotes.ui.components.SearchBarField
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteDetailEvent
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNoteClick: (Int) -> Unit = {},
    onNoteDismissed: (Note) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val allNotes by notesViewModel.allNotes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    HomeContent(
        modifier = modifier,
        notes = allNotes,
        onNoteClick = onNoteClick,
        onNoteDismissed = { note -> notesViewModel.onEvent(NoteDetailEvent.DeleteNote(note)) },
        searchQuery = searchQuery,
        onQueryChange = { searchQuery = it }
    )
}

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onNoteClick: (Int) -> Unit,
    onNoteDismissed: (Note) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    val listStateNotes = rememberLazyListState()

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val pinnedNotes = remember(filteredNotes) { filteredNotes.filter { it.isPinned } }
    val normalNotes = remember(filteredNotes) { filteredNotes.filter { !it.isPinned } }

    val groupedNormalNotes = remember(normalNotes) {
        val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

        normalNotes.groupBy { getNoteGroup(it.creationDate) }
            .toSortedMap(compareByDescending { header ->
                when (header) {
                    "Hoje" -> Calendar.getInstance().time
                    "Ontem" -> Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_YEAR, -1) }.time
                    else -> {
                        try {
                            monthYearFormat.parse(header) ?: Date(0)
                        } catch (e: Exception) {
                            Date(0)
                        }
                    }
                }
            })
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.background(Color.Transparent),
            verticalArrangement = Arrangement.Top,
        ) {
            ProfileBar()
            SearchBarField(
                modifier = modifier,
                query = searchQuery,
                onQueryChange = onQueryChange
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listStateNotes,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = MaterialTheme.dimens.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.paddingLarge)
            ) {
                groupedNormalNotes.forEach { (header, notesInGroup) ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        horizontal = MaterialTheme.dimens.paddingLarge,
                                        vertical = MaterialTheme.dimens.paddingSmall
                                    )
                            )
                        }
                    }
                    items(
                        items = notesInGroup,
                        key = { note -> "normal-${note.id}" },
                    ) { note ->
                        val formattedDate = remember(note.creationDate) {
                            dateFormatterRelative(note.creationDate.time)
                        }
                        Box(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.paddingLarge)) {
                            NoteItem(
                                id = note.id,
                                title = note.title,
                                content = note.content,
                                date = formattedDate,
                                category = note.category,
                                onClick = { onNoteClick(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getNoteGroup(date: Date): String {
    val calendar = Calendar.getInstance()
    val todayStart = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayStart = calendar.timeInMillis

    return when {
        date.time >= todayStart -> "Hoje"
        date.time >= yesterdayStart -> "Ontem"
        else -> {
            val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            monthYearFormat.format(date)
        }
    }
}



@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    GlyphNotesTheme {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        calendar.add(Calendar.MONTH, -1)
        val lastMonth = calendar.time

        val mockNotes = listOf(
            Note(id = 1, title = "Ideias para o App", content = "Explorar novas features de IA para o editor de notas.", tags = emptyList(), category = "Trabalho", isPinned = false, creationDate = today, lastEditDate = today),
            Note(id = 2, title = "Lista de Compras", content = "Leite, pão, ovos e café.", tags = emptyList(), category = "Pessoal", isPinned = false, creationDate = today, lastEditDate = today),
            Note(id = 3, title = "Resumo da Reunião", content = "Enviar o resumo sobre o alinhamento do projeto.", tags = emptyList(), category = "Trabalho", isPinned = false, creationDate = yesterday, lastEditDate = yesterday),
            Note(id = 4, title = "Filmes para Assistir", content = "Duna: Parte 2 e O Problema dos 3 Corpos.", tags = emptyList(), category = "Lazer", isPinned = false, creationDate = lastMonth, lastEditDate = lastMonth)
        )
        HomeContent(
            notes = mockNotes,
            onNoteClick = {},
            onNoteDismissed = {},
            searchQuery = "",
            onQueryChange = {}
        )
    }
}
