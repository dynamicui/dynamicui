package com.example.submodulekeyboardawarescreen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.xr.compose.testing.toDp
import com.example.submodulekeyboardawarescreen.ui.theme.SubmoduleKeyboardAwareScreenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubmoduleKeyboardAwareScreenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    SimpleScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SubmoduleKeyboardAwareScreenTheme {
        Greeting("Android")
    }
}
/*
fun Modifier.landscapeFloatingInput(
    value: String,
    onValueChange: (String) -> Unit
) = composed {

    val manager = LocalLandscapeInputManager.current
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val focusManager = LocalFocusManager.current

    this.onFocusChanged {
        if (it.isFocused && isLandscape && !manager.isVisible) {
            focusManager.clearFocus(force = true)   // 🔥 Prevent focus loop
            manager.show(value, onValueChange)
        }
    }
}*/



@Composable
fun SimpleScreen() {

    var username by remember { mutableStateOf("") }

    LandscapeInputProvider {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .landscapeFloatingInput(
                        value = username,
                        onValueChange = { username = it }
                    ),
                label = { Text("Username") }
            )

        }
    }
}

@Composable
fun FloatingInputHost(manager: LandscapeInputManager) {

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (!manager.isVisible || !isLandscape) return

    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = { manager.dismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),   // 🔥 Automatically stays above keyboard
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            TextField(
                value = manager.currentText,
                onValueChange = { manager.update(it) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}



val LocalLandscapeInputManager =
    staticCompositionLocalOf<LandscapeInputManager> {
        error("LandscapeInputManager not provided")
    }
@Composable
fun LandscapeInputProvider(content: @Composable () -> Unit) {

    val manager = remember { LandscapeInputManager() }

    CompositionLocalProvider(
        LocalLandscapeInputManager provides manager
    ) {
        Box(Modifier.fillMaxSize()) {
            content()
            FloatingInputHost(manager)
        }
    }
}

fun Modifier.landscapeFloatingInput(
    value: String,
    onValueChange: (String) -> Unit
) = composed {

    val manager = LocalLandscapeInputManager.current
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val focusManager = LocalFocusManager.current

    this.onFocusChanged {
        if (it.isFocused && isLandscape && !manager.isVisible) {
            manager.show(value, onValueChange)
            focusManager.clearFocus(force = true)
        }
    }
}

