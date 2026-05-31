package com.reysl.uroboros.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.reysl.uroboros.utils.MarkdownEditorController
import com.reysl.uroboros.utils.MarkdownEditorState
import com.reysl.uroboros.utils.rememberMarkdownToolbarUiState

@Composable
fun ConnectedMarkdownToolbar(
    state: MarkdownEditorState,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ui = rememberMarkdownToolbarUiState(state)

    RichTextToolbar(
        modifier = modifier,
        onBoldClick = { MarkdownEditorController.toggleBold(state) },
        onItalicClick = { MarkdownEditorController.toggleItalic(state) },
        onUnderlineClick = { MarkdownEditorController.toggleUnderline(state) },
        onStrikethroughClick = { MarkdownEditorController.toggleStrikethrough(state) },
        onTitleClick = { MarkdownEditorController.toggleTitle(state) },
        onSubtitleClick = { MarkdownEditorController.toggleSubtitle(state) },
        onTextColorClick = { },
        onLinkClick = onLinkClick,
        onCodeClick = { MarkdownEditorController.toggleInlineCode(state) },
        onCodeBlockClick = { MarkdownEditorController.insertCodeBlock(state) },
        onQuoteClick = { MarkdownEditorController.toggleQuote(state) },
        onBulletListClick = { MarkdownEditorController.toggleBulletList(state) },
        onNumberListClick = { MarkdownEditorController.toggleNumberList(state) },
        onUndoClick = { MarkdownEditorController.undo(state) },
        onRedoClick = { MarkdownEditorController.redo(state) },
        boldSelected = ui.boldSelected,
        italicSelected = ui.italicSelected,
        underlineSelected = false,
        strikethroughSelected = ui.strikethroughSelected,
        titleSelected = ui.titleSelected,
        subtitleSelected = ui.subtitleSelected,
        textColorSelected = false,
        linkSelected = false,
        codeSelected = ui.inlineCodeSelected,
        codeBlockSelected = false,
        quoteSelected = ui.quoteSelected,
        bulletListSelected = ui.bulletListSelected,
        numberListSelected = ui.numberListSelected,
    )
}
