@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.felipeserver.site.glyphnotes.ui.components

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.screens.NoteDetailScreenLauncherActivity

class NewNoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NewNoteWidget()
}

class NewNoteWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WidgetContent() {
    val context = LocalContext.current


    val action = actionStartActivity<NoteDetailScreenLauncherActivity>()

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(action)
            .background(GlanceTheme.colors.surface)
            .cornerRadius(24.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = GlanceModifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {

                // Shadow Layer REMOVED



                Box(
                    modifier = GlanceModifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ){
                     Image(
                        provider = ImageProvider(R.drawable.sidedshape8),
                        contentDescription = null,
                        modifier = GlanceModifier.fillMaxSize(),
                        colorFilter = androidx.glance.ColorFilter.tint(GlanceTheme.colors.primary)
                    )
                    
                     Text(
                        text = "+",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 32.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.padding(bottom = 4.dp) 
                    )
                }
            }
            
             Text(
                text = "New Note",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                modifier = GlanceModifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
@Preview(widthDp = 100, heightDp = 100)
fun NewNoteWidgetPreview() {
    GlanceTheme {
        WidgetContent()
    }
}