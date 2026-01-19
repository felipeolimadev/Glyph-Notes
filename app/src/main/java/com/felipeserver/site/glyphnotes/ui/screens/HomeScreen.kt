package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.components.NoteItem
import com.felipeserver.site.glyphnotes.ui.components.ProfileBar
import com.felipeserver.site.glyphnotes.ui.components.SearchBarField
import com.felipeserver.site.glyphnotes.ui.components.SwipeToRevealItem
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNoteClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val allNotes by notesViewModel.allNotes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    HomeContent(
        modifier = modifier,
        contentPadding = contentPadding,
        notes = allNotes,
        onNoteClick = onNoteClick,
        onNoteDelete = { note -> notesViewModel.onEvent(NoteDetailEvent.DeleteNote(note.id)) },
        searchQuery = searchQuery,
        onQueryChange = { searchQuery = it },
        selectedTags = selectedTags,
        onTagsChanged = { selectedTags = it },
        showFilterSheet = showFilterSheet,
        onFilterSheetChange = { showFilterSheet = it }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    notes: List<Note>,
    onNoteClick: (Int) -> Unit,
    onNoteDelete: (Note) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedTags: Set<String>,
    onTagsChanged: (Set<String>) -> Unit,
    showFilterSheet: Boolean,
    onFilterSheetChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val listStateNotes = rememberLazyListState()

    // Extract string resources before remember blocks to avoid composition issues
    val todayString = stringResource(R.string.today)
    val yesterdayString = stringResource(R.string.yesterday)
    val noteDeletedString = stringResource(R.string.note_dismissed)

    val filteredNotes = remember(notes, searchQuery, selectedTags) {
        notes.filter { note ->
            val matchesQuery = if (searchQuery.isBlank()) true else {
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
            val matchesTags = if (selectedTags.isEmpty()) true else {
                note.tags.any { it in selectedTags }
            }
            matchesQuery && matchesTags
        }
    }

    val allTags = remember(notes) {
        notes.flatMap { it.tags }.filter { it.isNotBlank() }.distinct().sorted()
    }
    
    val sheetState = rememberModalBottomSheetState()

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
            ProfileBar(contentPadding = contentPadding)
            SearchBarField(
                modifier = modifier,
                query = searchQuery,
                onQueryChange = onQueryChange,
                onFilterClick = { onFilterSheetChange(true) }
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listStateNotes,
                horizontalAlignment = Alignment.End,
                contentPadding = PaddingValues(
                    top = MaterialTheme.dimens.paddingLarge,
                    bottom = MaterialTheme.dimens.paddingLarge + contentPadding.calculateBottomPadding()
                ),
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
                        
                        // Swipe to reveal delete button - only deletes when button is clicked
                        Box(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.dimens.paddingLarge)
                        ) {
                            SwipeToRevealItem(
                                onDeleteClick = {
                                    onNoteDelete(note)
                                    Toast.makeText(context, noteDeletedString, Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                NoteItem(
                                    id = note.id,
                                    title = note.title,
                                    content = note.content,
                                    date = formattedDate,
                                    category = note.category,
                                    isPinned = note.isPinned,
                                    tags = note.tags,
                                    onClick = { onNoteClick(note.id) }
                                )
                            }
                        }
                    }
                }
            }
            }
        }
        
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { onFilterSheetChange(false) },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.dimens.paddingLarge)
                ) {
                    Text(
                        text = "Filtrar por Tags",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = MaterialTheme.dimens.paddingMedium)
                    )
                    
                    if (allTags.isEmpty()) {
                        Text(
                            text = "Nenhuma tag disponível.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allTags.forEach { tag ->
                                val isSelected = tag in selectedTags
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val newTags = if (isSelected) {
                                            selectedTags - tag
                                        } else {
                                            selectedTags + tag
                                        }
                                        onTagsChanged(newTags)
                                    },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(bottom = MaterialTheme.dimens.paddingLarge)) // Bottom spacing
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                tags = listOf("IA", "Dev", "Mobile"),
                category = "Trabalho",
                isPinned = true,
                creationDate = today,
                lastEditDate = today
            ),
            Note(
                id = 2,
                title = "Lista de Compras",
                content = "Leite, pão, ovos e café.",
                tags = listOf("Casa", "Mercado"),
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
        
        Scaffold(
            floatingActionButton = {
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
                            val isSelected = screen == Screen.Home
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
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            HomeContent(
                modifier = Modifier,
                contentPadding = innerPadding,
                notes = mockNotes,
                onNoteClick = {},
                onNoteDelete = {},
                searchQuery = "",
                onQueryChange = {},
                selectedTags = emptySet(),
                onTagsChanged = {},
                showFilterSheet = false,
                onFilterSheetChange = {}
            )
        }
    }
}
