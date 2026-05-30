package com.reysl.uroboros.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

data class MarkdownToolbarUiState(
    val boldSelected: Boolean = false,
    val italicSelected: Boolean = false,
    val strikethroughSelected: Boolean = false,
    val titleSelected: Boolean = false,
    val subtitleSelected: Boolean = false,
    val inlineCodeSelected: Boolean = false,
    val quoteSelected: Boolean = false,
    val bulletListSelected: Boolean = false,
    val numberListSelected: Boolean = false,
)

object MarkdownEditorController {

    fun toggleBold(state: MarkdownEditorState) = wrapSelection(state, "**", "**")

    fun toggleItalic(state: MarkdownEditorState) = wrapSelection(state, "*", "*")

    fun toggleStrikethrough(state: MarkdownEditorState) = wrapSelection(state, "~~", "~~")

    fun toggleInlineCode(state: MarkdownEditorState) = wrapSelection(state, "`", "`")

    fun toggleUnderline(state: MarkdownEditorState) = wrapSelection(state, "<u>", "</u>")

    fun toggleTitle(state: MarkdownEditorState) = toggleLineHeading(state, level = 1)

    fun toggleSubtitle(state: MarkdownEditorState) = toggleLineHeading(state, level = 2)

    fun toggleQuote(state: MarkdownEditorState) = toggleLinePrefix(state, "> ")

    fun toggleBulletList(state: MarkdownEditorState) = toggleLinePrefix(state, "- ")

    fun toggleNumberList(state: MarkdownEditorState) {
        val range = lineRangeForSelection(state)
        val block = state.text.substring(range.first, range.second)
        val lines = block.split('\n')
        val allNumbered = lines.all { NUMBERED_LIST.matches(it) }
        val newLines = if (allNumbered) {
            lines.map { NUMBERED_LIST.replace(it, "") }
        } else {
            lines.mapIndexed { index, line ->
                if (line.isBlank()) line else "${index + 1}. ${NUMBERED_LIST.replace(line, "")}"
            }
        }
        replaceBlock(state, range, newLines.joinToString("\n"))
    }

    fun insertCodeBlock(state: MarkdownEditorState) {
        val selected = selectedText(state)
        val block = buildString {
            append('\n')
            append("```")
            append('\n')
            append(selected)
            append('\n')
            append("```")
            append('\n')
        }
        insertAtSelection(state, block, cursorOffsetInInsert = 4)
    }

    fun insertLink(state: MarkdownEditorState, label: String, url: String) {
        val link = "[$label]($url)"
        if (!state.selection.collapsed) {
            wrapSelection(state, "[", "]($url)")
        } else {
            insertAtSelection(state, link, cursorOffsetInInsert = label.length + 3)
        }
    }

    fun undo(state: MarkdownEditorState) = state.undo()

    fun redo(state: MarkdownEditorState) = state.redo()

    fun toolbarUiState(state: MarkdownEditorState): MarkdownToolbarUiState {
        val lineStart = lineStartIndex(state, state.selection.start)
        val lineEnd = lineEndIndex(state, state.selection.end)
        val currentLine = state.text.substring(lineStart, lineEnd)

        return MarkdownToolbarUiState(
            boldSelected = isWrapped(state, "**", "**"),
            italicSelected = isWrapped(state, "*", "*") && !isWrapped(state, "**", "**"),
            strikethroughSelected = isWrapped(state, "~~", "~~"),
            titleSelected = currentLine.startsWith("# "),
            subtitleSelected = currentLine.startsWith("## "),
            inlineCodeSelected = isWrapped(state, "`", "`"),
            quoteSelected = currentLine.startsWith("> "),
            bulletListSelected = currentLine.startsWith("- "),
            numberListSelected = NUMBERED_LIST.matches(currentLine),
        )
    }

    private fun wrapSelection(state: MarkdownEditorState, prefix: String, suffix: String) {
        val text = state.text
        val selection = state.selection
        if (!selection.collapsed) {
            val selected = text.substring(selection.start, selection.end)
            if (selected.startsWith(prefix) && selected.endsWith(suffix)) {
                val unwrapped = selected.removePrefix(prefix).removeSuffix(suffix)
                val newText = text.replaceRange(selection.start, selection.end, unwrapped)
                state.applyEdit(
                    TextFieldValue(
                        text = newText,
                        selection = TextRange(selection.start, selection.start + unwrapped.length)
                    )
                )
                return
            }
            val wrapped = prefix + selected + suffix
            val newText = text.replaceRange(selection.start, selection.end, wrapped)
            state.applyEdit(
                TextFieldValue(
                    text = newText,
                    selection = TextRange(selection.start + prefix.length, selection.end + prefix.length)
                )
            )
        } else {
            val wrapped = prefix + suffix
            val newText = text.replaceRange(selection.start, selection.end, wrapped)
            state.applyEdit(
                TextFieldValue(
                    text = newText,
                    selection = TextRange(selection.start + prefix.length, selection.start + prefix.length)
                )
            )
        }
    }

    private fun toggleLineHeading(state: MarkdownEditorState, level: Int) {
        val prefix = "#".repeat(level) + " "
        val range = lineRangeForSelection(state)
        val block = state.text.substring(range.first, range.second)
        val lines = block.split('\n')
        val allHaveHeading = lines.all { it.startsWith(prefix) }
        val newLines = lines.map { line ->
            when {
                allHaveHeading && line.startsWith(prefix) -> line.removePrefix(prefix)
                line.isBlank() -> line
                else -> prefix + line.removePrefix("#".repeat(level) + " ")
            }
        }
        replaceBlock(state, range, newLines.joinToString("\n"))
    }

    private fun toggleLinePrefix(state: MarkdownEditorState, prefix: String) {
        val range = lineRangeForSelection(state)
        val block = state.text.substring(range.first, range.second)
        val lines = block.split('\n')
        val allHavePrefix = lines.all { it.startsWith(prefix) }
        val newLines = lines.map { line ->
            when {
                allHavePrefix && line.startsWith(prefix) -> line.removePrefix(prefix)
                line.isBlank() -> line
                else -> prefix + line
            }
        }
        replaceBlock(state, range, newLines.joinToString("\n"))
    }

    private fun replaceBlock(state: MarkdownEditorState, range: Pair<Int, Int>, newBlock: String) {
        val text = state.text
        val newText = text.replaceRange(range.first, range.second, newBlock)
        state.applyEdit(
            TextFieldValue(
                text = newText,
                selection = TextRange(range.first, range.first + newBlock.length)
            )
        )
    }

    private fun insertAtSelection(
        state: MarkdownEditorState,
        insert: String,
        cursorOffsetInInsert: Int,
    ) {
        val text = state.text
        val selection = state.selection
        val newText = buildString {
            append(text.substring(0, selection.start))
            append(insert)
            append(text.substring(selection.end))
        }
        val cursor = selection.start + cursorOffsetInInsert
        state.applyEdit(TextFieldValue(newText, TextRange(cursor, cursor)))
    }

    private fun selectedText(state: MarkdownEditorState): String {
        val selection = state.selection
        return if (selection.collapsed) {
            ""
        } else {
            state.text.substring(selection.start, selection.end)
        }
    }

    private fun isWrapped(state: MarkdownEditorState, prefix: String, suffix: String): Boolean {
        val selection = state.selection
        if (selection.collapsed) return false
        val selected = state.text.substring(selection.start, selection.end)
        return selected.startsWith(prefix) && selected.endsWith(suffix)
    }

    private fun lineRangeForSelection(state: MarkdownEditorState): Pair<Int, Int> {
        val start = lineStartIndex(state, state.selection.start)
        val end = lineEndIndex(state, state.selection.end)
        return start to end
    }

    private fun lineStartIndex(state: MarkdownEditorState, index: Int): Int {
        val text = state.text
        val safeIndex = index.coerceIn(0, text.length)
        return text.lastIndexOf('\n', safeIndex - 1).let { if (it == -1) 0 else it + 1 }
    }

    private fun lineEndIndex(state: MarkdownEditorState, index: Int): Int {
        val text = state.text
        val safeIndex = index.coerceIn(0, text.length)
        val nextBreak = text.indexOf('\n', safeIndex)
        return if (nextBreak == -1) text.length else nextBreak
    }

    private val NUMBERED_LIST = Regex("^\\d+\\.\\s+")
}

@Composable
fun rememberMarkdownToolbarUiState(state: MarkdownEditorState): MarkdownToolbarUiState {
    val text = state.text
    val selection = state.selection
    return remember(text, selection) {
        MarkdownEditorController.toolbarUiState(state)
    }
}
