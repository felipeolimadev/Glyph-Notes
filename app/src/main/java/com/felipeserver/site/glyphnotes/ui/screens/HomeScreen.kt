package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ssjetpackcomposeswipeableview.SwipeAbleItemView
import com.example.ssjetpackcomposeswipeableview.SwipeDirection
import com.felipeserver.site.glyphnotes.R
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

/**
 * Helper function para criar um Painter com tamanho customizado a partir de um ImageVector.
 * Útil quando bibliotecas não oferecem controle direto sobre o tamanho do ícone.
 *
 * @param image O ImageVector original (ex: Icons.Filled.Delete)
 * @param size O tamanho desejado em Dp
 */
@Composable
fun rememberSizedVectorPainter(
    image: ImageVector,
    size: Dp
) = rememberVectorPainter(
    defaultWidth = size,
    defaultHeight = size,
    viewportWidth = image.viewportWidth,
    viewportHeight = image.viewportHeight,
    name = image.name,
    tintColor = Color.Unspecified,
    tintBlendMode = image.tintBlendMode,
    autoMirror = image.autoMirror,
    content = { _, _ -> RenderVectorGroup(group = image.root) }
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNoteClick: (Int) -> Unit = {},
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
        onNoteDismissed = { note -> notesViewModel.onEvent(NoteDetailEvent.DeleteNote(note.id)) },
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
    val context = LocalContext.current
    val listStateNotes = rememberLazyListState()

    // Extract string resources before remember blocks to avoid composition issues
    val todayString = stringResource(R.string.today)
    val yesterdayString = stringResource(R.string.yesterday)
    val editRightClickedString = stringResource(R.string.edit_right_clicked)
    val noteDismissedString = stringResource(R.string.note_dismissed)

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

    val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    // Agrupa todas as notas por data (sem separar pinned/normal)
    val groupedNotes = remember(filteredNotes, todayString, yesterdayString) {

        filteredNotes.groupBy { getNoteGroup(it.creationDate, todayString, yesterdayString) }
            .toSortedMap(compareByDescending { header ->
                when (header) {
                    todayString -> Calendar.getInstance().time
                    yesterdayString -> Calendar.getInstance()
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
                groupedNotes.forEach { (header, notesInGroup) ->
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
                        key = { note -> "note-${note.id}" },
                    ) { note ->
                        val formattedDate = remember(note.creationDate) {
                            dateFormatterRelative(note.creationDate.time)
                        }
                        var itemHeight by remember { mutableStateOf<Dp?>(null) }
                        val density = LocalDensity.current

                        SwipeAbleItemView(
                            leftViewIcons = arrayListOf(),
                            rightViewIcons = arrayListOf(
                                Triple(rememberSizedVectorPainter(image = Icons.Filled.Delete, size = 32.dp), MaterialTheme.colorScheme.errorContainer, "btnDeleteRight")
                            ),
                            position = 0,
                            swipeDirection = SwipeDirection.LEFT,
                            onClick = {
                                when(it.second) {
                                    "btnEditRight" -> {
                                        Toast.makeText(context, editRightClickedString, Toast.LENGTH_SHORT).show()
                                    }
                                    "btnDeleteRight" -> {
                                        onNoteDismissed(note)
                                        Toast.makeText(context, noteDismissedString, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            leftViewWidth = 0.dp,
                            rightViewWidth = 140.dp,
                            height = itemHeight ?: 200.dp,
                            leftViewBackgroundColor = Color.Transparent,
                            rightViewBackgroundColor = MaterialTheme.colorScheme.background,
                            cornerRadius = 4.dp,
                            leftSpace = 0.dp,
                            rightSpace = 10.dp,
                            fractionalThreshold = 0.3f
                        )
                         {
                        Box(modifier = Modifier
                            .padding(horizontal = MaterialTheme.dimens.paddingLarge)
                            .onGloballyPositioned { coordinates ->
                                itemHeight = with(density) { coordinates.size.height.toDp() }
                            }) {
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
}

private fun getNoteGroup(date: Date, todayString: String, yesterdayString: String): String {
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
        date.time >= todayStart -> todayString
        date.time >= yesterdayStart -> yesterdayString
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
            Note(
                id = 1,
                title = "Ideias para o App",
                content = "Explorar novas features de IA para o editor de notas.",
                tags = emptyList(),
                category = "Trabalho",
                isPinned = false,
                creationDate = today,
                lastEditDate = today
            ),
            Note(
                id = 2,
                title = "Lista de Compras",
                content = "Leite, pão, ovos e café.",
                tags = emptyList(),
                category = "Pessoal",
                isPinned = false,
                creationDate = today,
                lastEditDate = today
            ),
            Note(
                id = 3,
                title = "Resumo da Reunião",
                content = "Enviar o resumo sobre o alinhamento do projeto.",
                tags = emptyList(),
                category = "Trabalho",
                isPinned = false,
                creationDate = yesterday,
                lastEditDate = yesterday
            ),
            Note(
                id = 4,
                title = "Filmes para Assistir",
                content = "Duna: Parte 2 e O Problema dos 3 Corpos.",
                tags = emptyList(),
                category = "Lazer",
                isPinned = false,
                creationDate = lastMonth,
                lastEditDate = lastMonth
            )
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
