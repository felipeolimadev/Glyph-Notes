package com.felipeserver.site.glyphnotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipeserver.site.glyphnotes.ui.theme.dimens

@Composable
fun PinnedCard(
    id: Int, title: String, content: String, date: String, category: String, onClick: () -> Unit
) {
    val initialColor = MaterialTheme.colorScheme.tertiaryContainer
    val finalColor = MaterialTheme.colorScheme.tertiary


    Surface(
        shape = RoundedCornerShape(24.dp), color = Color.Transparent
    ) {

        Card(
            onClick = onClick, colors = CardDefaults.cardColors(
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
                            ), text = category, overflow = TextOverflow.Ellipsis, maxLines = 1
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
                        text = date, color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

}