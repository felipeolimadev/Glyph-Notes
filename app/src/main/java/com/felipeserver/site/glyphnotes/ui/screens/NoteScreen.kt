package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteDetailEvent
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import java.util.Date

@Composable
fun NoteDetailScreen(id: Int, navController: NavController) {
    // Instancia o ViewModel com a Factory
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val uiState by notesViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = id) {
        notesViewModel.onEvent(NoteDetailEvent.LoadNote(id))
    }

    NoteDetailUi(
        title = uiState.title,
        content = uiState.content,
        lastEditDate = uiState.lastEditDate,
        onTitleChange = { newTitle ->
            notesViewModel.onEvent(NoteDetailEvent.OnTitleChange(newTitle))
        },
        onContentChange = { newContent ->
            notesViewModel.onEvent(NoteDetailEvent.OnContentChange(newContent))
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
    title: String,
    content: String,
    lastEditDate: Date,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
            )
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
                onValueChange = onContentChange)
        }
    }
}


@Composable
fun TagBar(modifier: Modifier = Modifier, tag: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall), text = tag
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TagBarPreview() {
    GlyphNotesTheme {
        TagBar(
            modifier = Modifier, tag = "teste"
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun NoteDetailPreview() {
    GlyphNotesTheme {
        NoteDetailUi(
            title = "Sample Note",
            content = "This is the content of the sample note.",
            lastEditDate = Date(),
            onTitleChange = {},
            onContentChange = {},
            onBackPress = {}
        )
    }
}
