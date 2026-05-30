package com.reysl.uroboros.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class MarkdownEditorState(initialMarkdown: String = "") {
    var value by mutableStateOf(TextFieldValue(initialMarkdown))
        private set

    val text: String get() = value.text
    val selection: TextRange get() = value.selection

    private val undoStack = ArrayDeque<TextFieldValue>()
    private val redoStack = ArrayDeque<TextFieldValue>()

    fun updateFromUser(newValue: TextFieldValue) {
        if (newValue.text != value.text) {
            pushUndo()
        }
        value = newValue
    }

    fun applyEdit(newValue: TextFieldValue) {
        pushUndo()
        value = newValue
    }

    fun undo() {
        val previous = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(value)
        value = previous
    }

    fun redo() {
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(value)
        value = next
    }

    private fun pushUndo() {
        undoStack.addLast(value)
        if (undoStack.size > MAX_HISTORY) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    fun clearContent() {
        undoStack.clear()
        redoStack.clear()
        value = TextFieldValue("")
    }

    companion object {
        private const val MAX_HISTORY = 50

        val Saver: Saver<MarkdownEditorState, String> = Saver(
            save = { it.text },
            restore = { MarkdownEditorState(it) }
        )
    }
}

@Composable
fun rememberMarkdownEditorState(initialMarkdown: String = ""): MarkdownEditorState {
    val normalized = MarkdownStorage.normalize(initialMarkdown)
    return rememberSaveable(normalized, saver = MarkdownEditorState.Saver) {
        MarkdownEditorState(normalized)
    }
}
