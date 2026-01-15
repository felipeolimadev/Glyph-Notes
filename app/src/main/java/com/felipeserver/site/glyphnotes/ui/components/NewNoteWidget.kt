@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.felipeserver.site.glyphnotes.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.LocalContext
import com.felipeserver.site.glyphnotes.R


class NewNoteWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget = NewNoteWidget()
}

class NewNoteWidget : GlanceAppWidget(){
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }
}
@Composable
fun WidgetContent(){
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ){
        Button(
            text = context.getString(R.string.add_note),
            onClick = {}
        )
        Text(
            text = context.getString(R.string.app_name),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface
            )
        )
    }
}
@Composable
@Preview(widthDp = 200, heightDp = 200)
fun NewNoteWidgetPreview(){
    GlanceTheme() {
        WidgetContent()
    }
}