package com.felipeserver.site.glyphnotes.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.theme.dimens

@Composable
fun CalendarScreen(modifier: Modifier = Modifier){
    Box (modifier = modifier
        .fillMaxSize()
        .padding(horizontal = MaterialTheme.dimens.paddingLarge),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = stringResource(R.string.calendar_screen),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}