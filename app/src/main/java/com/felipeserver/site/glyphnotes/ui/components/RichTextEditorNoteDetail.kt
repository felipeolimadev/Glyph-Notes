package com.felipeserver.site.glyphnotes.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.screens.TagItem
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteDetailEvent
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RichTextEditorNoteDetail(
    modifier: Modifier = Modifier,
    id: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val uiState by notesViewModel.uiState.collectAsState()
    val allTags by notesViewModel.allTags.collectAsState()

    val state = rememberRichTextState()
    state.config.linkColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(key1 = id) {
        notesViewModel.onEvent(NoteDetailEvent.LoadNote(id))
    }

    NoteRTE(
        modifier = modifier, state = state,
        title = uiState.title,
        content = uiState.content,
        tags = uiState.tags,
        allTags = allTags,
        lastEditDate = uiState.lastEditDate,
        isPinned = uiState.isPinned,
        onTitleChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnTitleChange(it))
        },
        onContentChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnContentChange(it))
        },
        onTagsChange = {
            notesViewModel.onEvent(NoteDetailEvent.OnTagsChange(it))
        },
        onDeleteTag = {
            notesViewModel.onEvent(NoteDetailEvent.OnDeleteTag(it))
        },
        onTogglePin = {
            notesViewModel.onEvent(NoteDetailEvent.TogglePin)
        },
        onBackPress = {
            notesViewModel.onEvent(NoteDetailEvent.OnBackPressed)

            navController.popBackStack()
        },
        onDeleteNote = {
            notesViewModel.onEvent(NoteDetailEvent.DeleteNote(id))
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRTE(
    modifier: Modifier = Modifier,
    state: RichTextState,
    currentSpanStyle: SpanStyle? = null,
    title: String,
    content: String,
    tags: List<String>,
    allTags: List<String>,
    lastEditDate: Date,
    isPinned: Boolean,
    onDeleteNote: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onDeleteTag: (String) -> Unit,
    onTogglePin: () -> Unit,
    onBackPress: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempSelectedTags by remember(tags) { mutableStateOf(tags) }

    var valueSearchMBS by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var showLinkDialog by rememberSaveable { mutableStateOf(false) }
    var linkUrl by rememberSaveable { mutableStateOf("") }
    
    // Estado para confirmação de exclusão de tag
    var showDeleteTagDialog by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf("") }
    
    // Guarda o último conteúdo sincronizado para evitar loops
    var lastSyncedContent by remember { mutableStateOf(content) }
    
    // Sincroniza o conteúdo externo (do ViewModel) para o RichTextState
    // Só executa quando o conteúdo muda de fora (ex: ao carregar uma nota)
    LaunchedEffect(content) {
        // Só sincroniza se o conteúdo mudou externamente (não por digitação)
        if (content != lastSyncedContent) {
            state.setMarkdown(content)
            lastSyncedContent = content
        }
    }

    // Sincroniza as mudanças do editor para o ViewModel
    LaunchedEffect(state.annotatedString) {
        val markdown = state.toMarkdown()
        // Só notifica se mudou e não é o mesmo que acabamos de sincronizar
        if (markdown != lastSyncedContent) {
            lastSyncedContent = markdown
            onContentChange(markdown)
        }
    }

    if (showLinkDialog) {
        AlertDialog(
            onDismissRequest = {
                showLinkDialog = false
                linkUrl = ""
            },
            title = { Text(stringResource(R.string.add_link)) },
            text = {
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("URL") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedText = state.annotatedString.text.substring(state.selection)
                        state.addLink(
                            text = if (selectedText.isNotEmpty()) selectedText else linkUrl,
                            url = linkUrl
                        )
                        showLinkDialog = false
                        linkUrl = ""
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLinkDialog = false
                        linkUrl = ""
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Diálogo de confirmação para exclusão de tag
    if (showDeleteTagDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteTagDialog = false
                tagToDelete = ""
            },
            title = { Text(stringResource(R.string.delete_tag_title)) },
            text = { Text(stringResource(R.string.delete_tag_confirmation, tagToDelete)) },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null)
            },
            confirmButton = {
                Button(onClick = {
                    onDeleteTag(tagToDelete)
                    showDeleteTagDialog = false
                    tagToDelete = ""
                }) {
                    Text(stringResource(R.string.delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteTagDialog = false
                    tagToDelete = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (showBottomSheet) {
        LaunchedEffect(tags) {
            tempSelectedTags = tags
        }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(

            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.select_tags),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item() {
                        TextField(

                            value = valueSearchMBS,
                            onValueChange = { newValue ->
                                valueSearchMBS = newValue
                                scope.launch {
                                    sheetState.expand()
                                }
                            },
                            label = { Text(stringResource(R.string.search_tags)) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight(500)
                            ),
                            maxLines = 1,

                            )
                    }
                    items(items = allTags.filter {
                        it.contains(
                            valueSearchMBS,
                            ignoreCase = true
                        )
                    }) { tag ->
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
                                    onTagsChange(tempSelectedTags)
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
                                    onTagsChange(tempSelectedTags)
                                }
                            )
                            Text(
                                text = tag,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )
                            IconButton(onClick = {
                                tagToDelete = tag
                                showDeleteTagDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_tag_desc)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para adicionar nova tag
                if (valueSearchMBS.isNotBlank() && !allTags.any { it.equals(valueSearchMBS, ignoreCase = true) }) {
                    Button(
                        onClick = {
                            val newTag = valueSearchMBS.trim()
                            val currentTags = tempSelectedTags.toMutableList()
                            if (!currentTags.contains(newTag)) {
                                currentTags.add(newTag)
                            }
                            tempSelectedTags = currentTags
                            onTagsChange(tempSelectedTags)
                            valueSearchMBS = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.add_tag_with_name, valueSearchMBS.trim()))
                    }
                }

            }
        }
    }
    Scaffold(
        modifier = modifier.imePadding(),
        topBar ={
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
                            contentDescription = stringResource(R.string.localized_description)
                        )
                    }
                },
                actions = {
                    val iconColor by animateColorAsState(
                        targetValue = if (isPinned) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = stringResource(R.string.pin_color_animation_label)
                    )
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.pin_note_desc),
                            tint = iconColor
                        )
                    }
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    if (showDeleteDialog == true) {
                        AlertDialog(
                            title = { Text(stringResource(R.string.delete_note_title)) },
                            text = { Text(stringResource(R.string.delete_note_confirmation)) },
                            icon = {
                                Icon(Icons.Default.Delete, stringResource(R.string.delete_icon_desc))
                            },
                            onDismissRequest = { showDeleteDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    onDeleteNote()
                                }) {
                                    Text(stringResource(R.string.delete_action))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            })
                    }
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_note_desc)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Bold Button
                IconButton(
                    onClick = {
                        state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.currentSpanStyle.fontWeight == FontWeight.Bold) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.currentSpanStyle.fontWeight == FontWeight.Bold) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        Icons.Filled.FormatBold,
                        contentDescription = stringResource(R.string.bold_format)
                    )
                }
                //Italic Button
                IconButton(
                    onClick = {
                        state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.currentSpanStyle.fontStyle == FontStyle.Italic) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.currentSpanStyle.fontStyle == FontStyle.Italic) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.FormatItalic,
                        contentDescription = stringResource(R.string.italic_format)
                    )
                }
                //Underline Button
                IconButton(
                    onClick = {
                        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.currentSpanStyle.textDecoration == TextDecoration.Underline) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.currentSpanStyle.textDecoration == TextDecoration.Underline) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.FormatUnderlined,
                        contentDescription = stringResource(R.string.underline_format)
                    )
                }
                //Ordererd List Button
                IconButton(
                    onClick = {
                        state.toggleOrderedList()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.isOrderedList) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.isOrderedList) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.FormatListNumbered,
                        contentDescription = stringResource(R.string.format_list_numbered)
                    )
                }
                //Unordererd List Button
                IconButton(
                    onClick = {
                        state.toggleUnorderedList()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.isUnorderedList) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.isUnorderedList) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                        contentDescription = stringResource(R.string.format_list_numbered)
                    )
                }
                //Link Button
                IconButton(
                    onClick = {
                        showLinkDialog = true
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.isLink) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (state.isLink) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = stringResource(R.string.format_list_numbered)
                    )
                }
            }
            
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_tag_desc)
                        )
                    }
                }
                items(items = tags) { tag ->
                    if (tag != "") {
                        TagItem(modifier = Modifier, tag = tag)
                    }
                }


            }
            TextField(
                value = title,
                placeholder = { Text(stringResource(R.string.title_placeholder)) },
                onValueChange = onTitleChange,
                label = { Text(text = stringResource(R.string.title_label)) },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight(500)
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                )
            )
            RichTextEditor(
                modifier = Modifier
                    .fillMaxSize(), state = state,
                placeholder = { Text(stringResource(R.string.content_placeholder)) },
                colors = RichTextEditorDefaults.richTextEditorColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RichTextEditorNoteDetailPreview() {
    GlyphNotesTheme {
        RichTextEditorNoteDetail(
            id = 1, navController = NavController(LocalContext.current)
        )
    }
}
