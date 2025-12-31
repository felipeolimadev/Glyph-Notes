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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.NavigationItem.Companion.navigationItems
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.NoteViewModel

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
fun HomeContent() {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileBar()
            SearchBarField()
            PinnedCards()
            NotesList()
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.padding(8.dp))
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
        modifier = Modifier.padding(5.dp, 5.dp),
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
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(filteredItems) { item ->
                Text(
                    text = item, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchQuery = item
                            active = false
                        }
                        .padding(vertical = 14.dp))
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PinnedCards() {
    val initialColor = Color(0xFF881337)
    val finalColor = Color(0xFF9F1239)

    Surface(
        shape = RoundedCornerShape(24.dp), color = Color.Transparent
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ), modifier = Modifier
                .size(200.dp)
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = initialColor

                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Surface(

                        shape = RoundedCornerShape(24.dp), color = finalColor.copy(alpha = 0.5f)

                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp, 6.dp, 8.dp, 6.dp), text = "Ideas"
                        )
                    }
                }
                //Parte de baixo
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Project Alpha",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                    Text(
                        text = "Update 2h ago"
                    )
                }
            }
        }
    }

}

@Composable
fun NotesList() {
    val notesViewModel: NoteViewModel = viewModel()
    val notes = notesViewModel.allNotes.collectAsState()

    LazyColumn() {
        items(notes.value) { note ->
            NotesItem(
                title = note.title,
                content = note.content,
                date = note.lastEditDate.time,
                category = note.category
            )
        }
    }
}

@Composable
fun NotesItem(title: String, content: String, date: Long, category: String) {

    Surface() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = "Note",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
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
}

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
