package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

data class DropdownItem(
    val icon: ImageVector,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(paddingValues: PaddingValues) {

    val themeList = listOf(
        DropdownItem(Icons.Default.ChevronRight, "Material You"),
        DropdownItem(Icons.Default.ChevronRight, "Glyph"),

        )
    var expanded by remember { mutableStateOf(false) }
    var selectedItemDropDown by remember { mutableStateOf(themeList[0]) }


    Scaffold(modifier = Modifier.padding(paddingValues), topBar = {
        TopAppBar(
            title = { Text("Settings") },
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ListItem(
                headlineContent = {
                    Text(text = "Theme")
                },
                supportingContent = { Text("Pick the default theme") },

                trailingContent = {
                    DropDownOptions()
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Theme Setting"
                    )
                })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DropDownOptions() {
    val checked by remember { mutableStateOf(false) }
    Switch(
        checked = checked,
        onCheckedChange = {}
    )
}


@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SettingsScreenPreview() {
    SettingsScreen(
        paddingValues = PaddingValues()
    )
}
