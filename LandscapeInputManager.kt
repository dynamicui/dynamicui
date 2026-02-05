package com.example.submodulekeyboardawarescreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class LandscapeInputManager {

    var currentText by mutableStateOf("")
    var isVisible by mutableStateOf(false)

    internal var onTextChange: ((String) -> Unit)? = null

    fun show(
        initialText: String,
        onValueChange: (String) -> Unit
    ) {
        currentText = initialText
        onTextChange = onValueChange
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
        onTextChange = null
    }

    fun update(text: String) {
        currentText = text
        onTextChange?.invoke(text)
    }
}
