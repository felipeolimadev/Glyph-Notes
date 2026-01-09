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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.components.BottomNavigationBar
import com.felipeserver.site.glyphnotes.ui.components.NoteItem
import com.felipeserver.site.glyphnotes.ui.components.ProfileBar
import com.felipeserver.site.glyphnotes.ui.components.SearchBarField
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
    // Adicionando argumentos opcionais para facilitar a pré-visualização sem inicializar o ViewModel
    onNoteClick: (Int) -> Unit = {},
    onFabClick: () -> Unit = {},
    onNoteDismissed: (Note) -> Unit = {},
    initialNotes: List<Note> = emptyList(),
    initialRoute: String = Screen.Home.rout
) {
    // Navigation Setup
    val navController = rememberNavController()

    // ViewModel Setup (Injeção de Estado)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val allNotes by notesViewModel.allNotes.collectAsState()

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Use initialNotes se estiver em preview, caso contrário use o estado do ViewModel
    val notesToDisplay = if (initialNotes.isNotEmpty()) initialNotes else allNotes

    // Callbacks para interagir com o ViewModel
    val noteClicked: (Int) -> Unit = { noteId ->
        if (initialNotes.isNotEmpty()) {
            // Em preview, apenas simula a navegação (ou chama o callback passado)
            onNoteClick(noteId)
        } else {
            navController.navigate("note_screen/$noteId")
        }
    }

    val fabClicked: () -> Unit = {
        if (initialNotes.isNotEmpty()) {
            onFabClick()
        } else {
            navController.navigate("note_screen/-1")
        }
    }

    val noteDismissed: (Note) -> Unit = { note ->
        if (initialNotes.isNotEmpty()) {
            onNoteDismissed(note)
        } else {
            notesViewModel.onEvent(NoteDetailEvent.DeleteNote(note))
        }
    }

    // UI Structure (Scaffold e NavHost)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: initialRoute

    val bottomBarScreens = listOf(
        Screen.Home.rout, Screen.Favorite.rout, Screen.Calendar.rout, Screen.Settings.rout
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (currentRoute in bottomBarScreens) {
                FloatingActionButton(
                    onClick = fabClicked,
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, startDestination = initialRoute
        ) {
            composable(route = Screen.Home.rout) {
                HomeContent(
                    notes = notesToDisplay,
                    onNoteClick = noteClicked,
                    paddingValues = innerPadding,
                    onNoteDismissed = noteDismissed,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
            composable(route = Screen.Favorite.rout) {
                FavoritesScreen(
                    notes = notesToDisplay,
                    onNoteClick = noteClicked,
                    paddingValues = innerPadding
                )
            }
            composable(route = Screen.Calendar.rout) {
                CalendarScreen(paddingValues = innerPadding)
            }
            composable(route = Screen.Settings.rout) {
                SettingsScreen(paddingValues = innerPadding)
            }
            composable(
                route = "note_screen/{noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId")
                if (noteId != null) {
                    NoteDetailScreen(id = noteId, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    notes: List<Note>,
    onNoteClick: (Int) -> Unit,
    paddingValues: PaddingValues,
    onNoteDismissed: (Note) -> Unit,
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    // Estado puramente de UI (scroll) que pertence a este Composable Stateless
    val listStateNotes = rememberLazyListState()

    // Filtra as notas com base na query de busca
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

    // Separa as notas entre fixadas e normais
    val pinnedNotes = remember(filteredNotes) { filteredNotes.filter { it.isPinned } }
    val normalNotes = remember(filteredNotes) { filteredNotes.filter { !it.isPinned } }

    // Agrupa as notas normais por data para a timeline
    val groupedNormalNotes = remember(normalNotes) {
        val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

        normalNotes.groupBy { getNoteGroup(it.lastEditDate) }
            // Ordena os grupos em ordem cronológica inversa (mais recente primeiro)
            .toSortedMap(compareByDescending { header ->
                when (header) {
                    "Hoje" -> Calendar.getInstance().time // Data de hoje para ordenação máxima
                    "Ontem" -> Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time // Data de ontem
                    else -> {
                        // Converte "MM/yyyy" de volta para uma data para ordenação
                        try {
                            monthYearFormat.parse(header) ?: Date(0)
                        } catch (e: Exception) {
                            Date(0) // Em caso de erro de parse
                        }
                    }
                }
            })
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.background(Color.Transparent),
            verticalArrangement = Arrangement.Top,
        ) {
            ProfileBar()
            SearchBarField(
                query = searchQuery,
                onQueryChange = onQueryChange
            )

            // A timeline agora é a visualização principal
            LazyColumn(
                modifier = Modifier.weight(1f), // Garante que a lista ocupe o espaço disponível e seja rolável
                state = listStateNotes,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = MaterialTheme.dimens.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.paddingLarge)
            ) {
                // Seção da timeline com cabeçalhos "grudentos"
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
                                    .padding(horizontal = MaterialTheme.dimens.paddingLarge, vertical = MaterialTheme.dimens.paddingSmall)
                            )
                        }
                    }
                    items(
                        items = notesInGroup,
                        key = { note -> "normal-${note.id}" },
                    ) { note ->
                        val formattedDate = remember(note.lastEditDate) {
                            dateFormatterRelative(note.lastEditDate.time)
                        }
                        // Usando um padding horizontal por item para centralizar visualmente
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
            // Usa o formato Mês/Ano para as demais datas
            val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            monthYearFormat.format(date)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    GlyphNotesTheme {
        val mockNotes = listOf(
            Note(
                id = 1,
                title = "Preview Note 1",
                content = "Esta é uma nota de exemplo para o preview.",
                tags = emptyList(),
                category = "Preview",
                isPinned = true,
                creationDate = Date(),
                lastEditDate = Date()
            ), Note(
                id = 2,
                title = "Preview Note 2",
                content = "Esta é outra nota de exemplo.",
                tags = emptyList(),
                category = "Work",
                isPinned = false,
                creationDate = Date(),
                lastEditDate = Date()
            )
        )

        // Chamando o novo HomeScreen consolidado, mas passando dados mockados
        HomeScreen(initialNotes = mockNotes, initialRoute = Screen.Home.rout)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeContentPreview() {
    GlyphNotesTheme {
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
    }
}
