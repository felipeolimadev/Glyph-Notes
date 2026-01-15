package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme

data class DropdownItem(val icon: ImageVector, val title: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    Surface(modifier = modifier.fillMaxSize()) {
        Column {
            TopAppBar(title = { Text(stringResource(R.string.settings_title)) })
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.settings_theme)) },
                supportingContent = { Text(stringResource(R.string.settings_pick_default_theme)) },
                trailingContent = { DropDownOptions() },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = stringResource(R.string.settings_theme_setting_desc)
                    )
                },
            )
        }
    }
}

@Composable
fun DropDownOptions() {
    val checked by remember { mutableStateOf(false) }
    Switch(checked = checked, onCheckedChange = {})
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SettingsScreenPreview() {
    GlyphNotesTheme {
        SettingsScreen()
    }
}
