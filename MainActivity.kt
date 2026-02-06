package com.example.submodulekeyboardawarescreen

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.submodulekeyboardawarescreen.ui.theme.SubmoduleKeyboardAwareScreenTheme
import kotlinx.coroutines.delay

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
//                    SimpleScreen()
                    MultiFieldScreen()
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LandscapeInputProvider {
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
fun MultiFieldScreen() {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LandscapeInputProvider {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .landscapeFloatingInput(
                        value = username,
                        onValueChange = { username = it }
                    )
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .landscapeFloatingInput(
                        value = email,
                        onValueChange = { email = it }
                    )
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .landscapeFloatingInput(
                        value = password,
                        onValueChange = { password = it }
                    )
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
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = { manager.dismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val dialogWindow =
            (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            dialogWindow?.setDimAmount(0.25f)
        }

        TextField(
            value = manager.currentText,
            onValueChange = { manager.update(it) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    manager.dismiss()
                }
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusRequester)
        )
    }

    LaunchedEffect(manager.isVisible) {
        delay(50)
        focusRequester.requestFocus()
        keyboardController?.show()
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

