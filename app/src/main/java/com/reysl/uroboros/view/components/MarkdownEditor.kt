package com.reysl.uroboros.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.reysl.uroboros.ui.theme.appCodeBlockBackground
import com.reysl.uroboros.ui.theme.appCodeBlockLanguage
import com.reysl.uroboros.ui.theme.appCodeBlockText
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.utils.MarkdownEditorState
import com.reysl.uroboros.utils.MarkdownVisualTransformation

@Composable
fun MarkdownEditor(
    state: MarkdownEditorState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    placeholder: String? = null,
    placeholderStyle: TextStyle = textStyle,
) {
    val codeBlockBackground = appCodeBlockBackground()
    val codeBlockText = appCodeBlockText()
    val fenceColor = appCodeBlockLanguage()

    val visualTransformation = remember(codeBlockBackground, codeBlockText, fenceColor) {
        MarkdownVisualTransformation(
            codeBlockBackground = codeBlockBackground,
            codeBlockTextColor = codeBlockText,
            fenceColor = fenceColor,
        )
    }

    BasicTextField(
        value = state.value,
        onValueChange = { state.updateFromUser(it) },
        modifier = modifier.fillMaxSize(),
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(appGreen()),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (placeholder != null && state.text.isEmpty()) {
                    Text(text = placeholder, style = placeholderStyle)
                }
                innerTextField()
            }
        },
    )
}
