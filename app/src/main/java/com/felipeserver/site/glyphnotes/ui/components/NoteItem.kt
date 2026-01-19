package com.felipeserver.site.glyphnotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.dateFormatterRelative
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import java.util.Date

@Composable
fun NoteItem(
    id: Int, 
    title: String, 
    content: String, 
    date: String, 
    category: String, 
    isPinned: Boolean,
    tags: List<String>,
    onClick: () -> Unit
) {

    val state = rememberRichTextState()
    state.setMarkdown(content)
    // Calculate max lines based on content length
    val maxLines = when {
        content.length > 200 -> 10
        content.length > 100 -> 6
        else -> 3
    }
    
    // Strip markdown syntax for plain text preview to avoid showing raw markdown
    val plainTextContent = remember(content) {
        content
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1") // Bold
            .replace(Regex("\\*(.+?)\\*"), "$1")       // Italic
            .replace(Regex("__(.+?)__"), "$1")         // Bold alt
            .replace(Regex("_(.+?)_"), "$1")           // Italic alt
            .replace(Regex("~~(.+?)~~"), "$1")         // Strikethrough  
            .replace(Regex("\\[(.+?)\\]\\(.+?\\)"), "$1") // Links
            .replace(Regex("^#+\\s*", RegexOption.MULTILINE), "") // Headers
            .replace(Regex("^[-*+]\\s+", RegexOption.MULTILINE), "• ") // Unordered lists
            .replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "") // Ordered lists
            .trim()
    }
    
    Card(
        onClick = onClick
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
                        contentDescription = stringResource(R.string.note_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall))

//                    Text(
//                        text = category,
//                        style = MaterialTheme.typography.titleSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        fontWeight = FontWeight.Bold
//                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isPinned) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(MaterialTheme.dimens.paddingSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Use plain text preview instead of RichText to avoid prefetch crashes
            // The RichText component causes IllegalArgumentException during LazyList prefetch
            // due to race conditions in RichTextState.adjustRichParagraphLayout

            RichText(
                state = state,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(
                modifier = Modifier.padding(MaterialTheme.dimens.paddingLarge)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
               val validTags = remember(tags) { tags.filter { it.isNotBlank() } }

               if(validTags.isNotEmpty()){
                   LazyRow(
                       horizontalArrangement = Arrangement.spacedBy(4.dp),
                       modifier = Modifier.weight(1f)
                   ) {
                       items(validTags.size) { index ->
                           SuggestionChip(
                               onClick = { /*TODO*/ },
                               label = { Text(validTags[index]) }
                           )
                       }
                   }
               }
                
                Spacer(modifier = Modifier.weight(0.1f))

                Text(
                    text = date,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    // Importar GlyphNotesTheme para o preview funcionar corretamente
    GlyphNotesTheme {
        val mockDate = remember { dateFormatterRelative(Date().time) }
        NoteItem(
            id = 1,
            title = "Título de Exemplo",
            content = "Este é o conteúdo longo que será truncado com elipses se exceder duas linhas.",
            date = mockDate,
            category = "Componente",
            isPinned = true,
            tags = listOf("Tag 1", "Tag 2"),
            onClick = {}
        )
    }
}