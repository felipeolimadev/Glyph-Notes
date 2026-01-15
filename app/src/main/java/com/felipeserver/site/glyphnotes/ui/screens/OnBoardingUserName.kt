package com.felipeserver.site.glyphnotes.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felipeserver.site.glyphnotes.R
import com.felipeserver.site.glyphnotes.ui.theme.GlyphNotesTheme
import com.felipeserver.site.glyphnotes.ui.theme.dimens
import com.felipeserver.site.glyphnotes.ui.viewmodel.navigation.Screen
import com.felipeserver.site.glyphnotes.ui.viewmodel.ui.saveUsername
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingUserName(navController: NavController) {
    val state = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(MaterialTheme.dimens.paddingLarge)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Column() {
                Spacer(modifier = Modifier.size(15.dp))
                Text(
                    stringResource(R.string.onboarding_hello),
                    style = MaterialTheme.typography.displayLargeEmphasized,
                    textAlign = TextAlign.Start
                )
                Text(
                    stringResource(R.string.onboarding_what_should_we_call_you),
                    style = MaterialTheme.typography.headlineSmall,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                state = state,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                ),
                placeholder = { Text(stringResource(R.string.onboarding_enter_your_name)) },
                label = {Text(stringResource(R.string.onboarding_your_identity_in_the_app))}
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    scope.launch {
                        saveUsername(context, state.text.toString())
                        navController.navigate(Screen.Home.rout){
                            popUpTo(Screen.OnBoarding.rout) {
                                inclusive = true
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.onboarding_continue))
            }

        }


    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun OnBoardingUserNamePreview() {
    GlyphNotesTheme {
        OnBoardingUserName(navController = NavController(LocalContext.current))
    }
}
