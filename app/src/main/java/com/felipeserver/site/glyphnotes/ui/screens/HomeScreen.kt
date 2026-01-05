package com.felipeserver.site.glyphnotes.ui.screens


import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDatabase
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.NavigationItem.Companion.navigationItems
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModelFactory
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatter
import java.util.Date

@Composable
fun HomeScreen() {
    //Navigation
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(
            onClick = {},
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }, floatingActionButtonPosition = FabPosition.End, bottomBar = {
        BottomNavigationBar(navController)
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.rout,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.rout) {
                HomeContent()
            }
            composable(route = Screen.Folder.rout) {
                FolderScreen()
            }
            composable(route = Screen.Calendar.rout) {
                CalendarScreen()
            }
            composable(route = Screen.Settings.rout) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun HomeContent(
    notesPreview: List<Note>? = null // Novo parâmetro para previews
) {
    // Instancia o ViewModel com a Factory
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteDao = NoteDatabase.getDatabase(context, scope).noteDao()
    val factory = NoteViewModelFactory(noteDao)
    val notesViewModel: NoteViewModel = viewModel(factory = factory)
    val notesState = if (notesPreview != null) {

        remember { mutableStateOf(notesPreview) }
    } else {

        notesViewModel.allNotes.collectAsState()
    }
    val notes by notesState

    val listState = rememberLazyListState()



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            ProfileBar()
            SearchBarField()
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = MaterialTheme.dimens.paddingSmall),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.listSpacing)
            ) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = MaterialTheme.dimens.paddingLarge),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.paddingLarge)
                    ) {
                        items(
                            items = notes, key = { note -> note.id }) { note ->
                            PinnedCard(
                                title = note.title,
                                content = note.content,
                                date = note.lastEditDate.time,
                                category = note.category
                            )
                        }
                    }
                }
                items(
                    items = notes, key = { note -> note.id }) { note ->
                    Box(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.paddingLarge)) {
                        val dateFixed = remember(note.lastEditDate) { dateFormatter(note.lastEditDate) }
                        NotesItem(
                            title = note.title,
                            content = note.content,
                            date = dateFixed,
                            category = note.category
                        )
                    }
                }
            }
        }


    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier // Aplica o modifier recebido
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.paddingLarge),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
        )
        Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingMedium))
        Column {
            Text(
                text = "Welcome back, ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Felipe",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarField() {

    var searchQuery by remember { mutableStateOf("") }
    val items =
        listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew")
    var active by remember { mutableStateOf(false) }
    val filteredItems = items.filter { it.contains(searchQuery, ignoreCase = true) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.paddingLarge),
        query = searchQuery,
        onQueryChange = { newQuery: String ->
            searchQuery = newQuery
        },
        onSearch = { _ ->
            // Ação a ser tomada quando o usuário pressiona "Enter" ou o ícone de busca
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = {
            Text("Search notes...")
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (active) {
                IconButton(onClick = {
                    if (searchQuery.isNotEmpty()) {
                        searchQuery = ""
                    } else {
                        active = false
                    }
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        }) {
        // Conteúdo exibido quando a SearchBar está ativa (Resultados)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(
                horizontal = MaterialTheme.dimens.paddingLarge,
                vertical = MaterialTheme.dimens.paddingMedium
            )
        ) {
            items(items = filteredItems, key = { it }) { item ->
                Text(text = item, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        searchQuery = item
                        active = false
                    }
                    .padding(vertical = MaterialTheme.dimens.paddingLarge))
                HorizontalDivider()
            }
        }
    }
    Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall))
}


@Composable
fun PinnedCard(title: String, content: String, date: Long, category: String) {
    val initialColor = MaterialTheme.colorScheme.tertiaryContainer
    val finalColor = MaterialTheme.colorScheme.tertiary


    Surface(
        shape = RoundedCornerShape(24.dp), color = Color.Transparent
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ), modifier = Modifier
                .size(170.dp)
                .border(
                    width = 1.dp,
                    color = initialColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )

                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            initialColor.copy(alpha = 0.3f), finalColor.copy(alpha = 0.2f)
                        ), start = Offset.Zero, end = Offset.Infinite

                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Parte de cima
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.dimens.paddingLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = initialColor

                    )
                    Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingMedium))
                    Surface(

                        shape = RoundedCornerShape(24.dp), color = finalColor.copy(alpha = 0.5f)

                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = MaterialTheme.dimens.paddingMedium,
                                vertical = MaterialTheme.dimens.paddingSmall
                            ),
                            text = category,
                            overflow = TextOverflow.Ellipsis, maxLines = 1
                        )
                    }
                }
                //Parte de baixo
                Column(
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.paddingLarge)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                    Text(
                        text = dateFormatter(date), color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

}


@Composable
fun NotesItem(title: String, content: String, date: String, category: String) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.paddingLarge)
        ) {
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = "Note",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall))

                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    NavigationBar(
        containerColor = BottomAppBarDefaults.containerColor,
        tonalElevation = BottomAppBarDefaults.ContainerElevation,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index, onClick = {
                selectedNavigationIndex.intValue = index
                navController.navigate(item.route)
            }, icon = {
                Icon(item.icon, contentDescription = item.title)
            }, label = {
                Text(
                    item.title,
                    color = if (index == selectedNavigationIndex.intValue) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
            )
            )
        }
    }
}/*
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NotesItemPreview() {
    GlyphNotesTheme {
        NotesItem(
            title = "My grocery list for the week",
            content = "Don't forget the milk! Also need to buy some bread, cheese, ham, and some fruits like apples and bananas. Maybe some yogurt too.",
            date = 1678886400000L,
            category = "Personal"
        )
    }
}



@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
GlyphNotesTheme {
HomeScreen()
}

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileBarPreview() {
    GlyphNotesTheme {
        ProfileBar()
    }
}
*/

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeContentPreview() {
    GlyphNotesTheme {
        // Simula dados para evitar chamar o viewModel() em preview
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

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeContent(notesPreview = mockNotes)
        }
    }
}