package com.felipeserver.site.glyphnotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteDetailEvent
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import java.util.Date

/**
 * Tela "inteligente" que funciona como um contêiner para a visualização de detalhes da nota.
 * É responsável por obter dados do [NoteViewModel], coletar o estado da UI e lidar com a navegação.
 *
 * @param id O ID da nota a ser exibida. Se o ID for -1, a tela abre em modo de criação para uma nova nota.
 * @param navController O [NavController] usado para navegar de volta da tela.
 */
@Composable
fun NoteDetailScreen(id: Int, navController: NavController) {
    // Instancia o ViewModel com a Factory
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val uiState by notesViewModel.uiState.collectAsState()
    val allTags by notesViewModel.allTags.collectAsState()

    LaunchedEffect(key1 = id) {
        notesViewModel.onEvent(NoteDetailEvent.LoadNote(id))
    }

    NoteDetailUi(
        title = uiState.title,
        content = uiState.content,
        tags = uiState.tags,
        allTags = allTags,
        lastEditDate = uiState.lastEditDate,
        onTitleChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnTitleChange(it))
        },
        onContentChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnContentChange(it))
        },
        onTagsChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnTagsChange(it))
        },
        onBackPress = {
            notesViewModel.onEvent(NoteDetailEvent.OnBackPressed)

            navController.popBackStack()
        }
    )
}

/**
 * Composable "burro" e sem estado, responsável por renderizar a interface do usuário da tela de detalhes da nota.
 * Ele exibe os dados da nota e delega todos os eventos de interação do usuário para o chamador por meio de funções lambda.
 *
 * @param title O título atual da nota a ser exibido no campo de texto do título.
 * @param content O conteúdo principal da nota a ser exibido no campo de texto do conteúdo.
 * @param tags A lista de tags atualmente associadas à nota.
 * @param allTags Uma lista completa de todas as tags disponíveis no aplicativo, usada para o ModalBottomSheet de seleção de tags.
 * @param lastEditDate A data da última edição da nota, exibida na barra de aplicativos superior.
 * @param onTitleChange Um retorno de chamada invocado quando o usuário digita no campo de texto do título.
 * @param onContentChange Um retorno de chamada invocado quando o usuário digita no campo de texto do conteúdo.
 * @param onTagsChange Um retorno de chamada invocado quando o usuário confirma sua seleção de tags no ModalBottomSheet.
 * @param onBackPress Um retorno de chamada invocado quando o usuário toca no ícone de navegação para voltar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailUi(
    title: String,
    content: String,
    tags: List<String>,
    allTags: List<String>,
    lastEditDate: Date,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onBackPress: () -> Unit,

    ) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempSelectedTags by remember(tags) { mutableStateOf(tags) }

    if (showBottomSheet) {
        LaunchedEffect(tags) {
            tempSelectedTags = tags
        }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Select Tags",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = allTags) { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val currentTags = tempSelectedTags.toMutableList()
                                    if (currentTags.contains(tag)) {
                                        currentTags.remove(tag)
                                    } else {
                                        currentTags.add(tag)
                                    }
                                    tempSelectedTags = currentTags
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = tag in tempSelectedTags,
                                onCheckedChange = { isChecked ->
                                    val currentTags = tempSelectedTags.toMutableList()
                                    if (isChecked) {
                                        if (!currentTags.contains(tag)) currentTags.add(tag)
                                    } else {
                                        currentTags.remove(tag)
                                    }
                                    tempSelectedTags = currentTags
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(tag)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showBottomSheet = false },
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {                        onTagsChange(tempSelectedTags)
                        showBottomSheet = false
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(top = 0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.last_edited),
                            style = MaterialTheme.typography.labelLarge
                        )
                        val formattedDate = remember(lastEditDate) {
                            dateFormatterRelative(lastEditDate.time)
                        }
                        Text(
                            text = formattedDate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text(text = "Title") },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight(500)
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = tags) { tag ->
                    if (tag == "") {
                    } else {
                        TagItem(modifier = Modifier, tag = tag)
                    }
                }

                item {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Tag"
                        )
                    }
                }
            }
            TextField(
                modifier = Modifier.fillMaxSize(),
                placeholder = { Text(text = "Content") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                value = content,
                onValueChange = onContentChange
            )
        }
    }
}

@Composable
fun TagItem(modifier: Modifier, tag: String) {
    FilterChip(
        modifier = modifier,
        onClick = { /*TODO*/ },
        label = {
            Text(text = tag, style = MaterialTheme.typography.labelSmall)
        },
        selected = false
    )
}


@Preview(name = "Empty Note", showBackground = true)
@Composable
private fun NoteDetailUiPreview_Empty() {
    GlyphNotesTheme {
        NoteDetailUi(
            title = "",
            content = "",
            tags = emptyList(),
            allTags = listOf("Work", "Personal", "Urgent"),
            lastEditDate = Date(),
            onTitleChange = {},
            onContentChange = {},
            onTagsChange = {},
            onBackPress = {}
        )
    }
}

@Preview(name = "Filled Note", showBackground = true, uiMode = 3)
@Composable
private fun NoteDetailUiPreview_Filled() {
    GlyphNotesTheme {
        NoteDetailUi(
            title = "Team Meeting Recap",
            content = "Here are the key takeaways from our meeting today:\n\n- Q3 roadmap is finalized.\n- Budget proposal needs minor adjustments.\n- The team offsite is scheduled for next month.",
            tags = listOf("Work", "Urgent"),
            allTags = listOf("Work", "Personal", "Urgent", "Ideas"),
            lastEditDate = Date(),
            onTitleChange = {},
            onContentChange = {},
            onTagsChange = {},
            onBackPress = {}
        )
    }
}
