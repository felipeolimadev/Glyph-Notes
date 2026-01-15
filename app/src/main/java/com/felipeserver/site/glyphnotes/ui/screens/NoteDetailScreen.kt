package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun NoteDetailScreen(
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

    LaunchedEffect(key1 = id) {
        notesViewModel.onEvent(NoteDetailEvent.LoadNote(id))
    }
    NoteDetailUi(
        modifier = modifier,
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailUi(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    tags: List<String>,
    allTags: List<String>,
    lastEditDate: Date,
    isPinned: Boolean,
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


    if (showBottomSheet) {
        LaunchedEffect(tags) {
            tempSelectedTags = tags
        }
// TODO: Adicionar botão para incluir novas tags
// TODO: Adicionar botão para remover tags ao lado de cada tag
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(

            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Select Tags",
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
                            label = { Text("Search tags...") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight(500)
                            ),
                            maxLines = 1,

                        )
                    }
                    items(items = allTags.filter { it.contains(valueSearchMBS, ignoreCase = true) }) { tag ->
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
                            IconButton(onClick = { onDeleteTag(tag) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Tag"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


            }
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                    val iconColor by animateColorAsState(
                        targetValue = if (isPinned) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "Pin color animation"
                    )
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Pin note",
                            tint = iconColor
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(text = "Title") },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight(500)
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = MaterialTheme.colorScheme.background,
                )
            )

            TextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = { Text("Content") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent
                )
            )

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
                            contentDescription = "Add Tag"
                        )
                    }
                }
                items(items = tags) { tag ->
                    if (tag != "") {
                        TagItem(modifier = Modifier, tag = tag)
                    }
                }


            }
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


@Preview(name = "Empty Note", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteDetailUiPreview_Empty() {
    GlyphNotesTheme {
        NoteDetailUi(
            title = "",
            content = "",
            tags = emptyList(),
            allTags = listOf("Work", "Personal", "Urgent"),
            lastEditDate = Date(),
            isPinned = false,
            onTitleChange = {},
            onContentChange = {},
            onTagsChange = {},
            onDeleteTag = {},
            onTogglePin = {},
            onBackPress = {}
        )
    }
}

@Preview(name = "Filled Note", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteDetailUiPreview_Filled() {
    GlyphNotesTheme {
        NoteDetailUi(
            title = "Team Meeting Recap",
            content = """Here are the key takeaways from our meeting today:

- Q3 roadmap is finalized.
- Budget proposal needs minor adjustments.
- The team offsite is sche""",
            tags = listOf("Work", "Meetings", "Q3"),
            allTags = listOf("Work", "Meetings", "Q3", "Personal", "Urgent"),
            lastEditDate = Date(),
            isPinned = true,
            onTitleChange = {},
            onContentChange = {},
            onTagsChange = {},
            onDeleteTag = {},
            onTogglePin = {},
            onBackPress = {}
        )
    }
}
