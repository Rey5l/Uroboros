package com.reysl.uroboros.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R

@Composable
fun RichTextToolbar(
    modifier: Modifier = Modifier,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onTextColorClick: () -> Unit,
    onLinkClick: () -> Unit,
    onCodeClick: () -> Unit,
    onCodeBlockClick: () -> Unit,
    onQuoteClick: () -> Unit,
    onBulletListClick: () -> Unit,
    onNumberListClick: () -> Unit,
    onAlignLeftClick: () -> Unit,
    onAlignCenterClick: () -> Unit,
    onAlignRightClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    boldSelected: Boolean = false,
    italicSelected: Boolean = false,
    underlineSelected: Boolean = false,
    strikethroughSelected: Boolean = false,
    titleSelected: Boolean = false,
    subtitleSelected: Boolean = false,
    textColorSelected: Boolean = false,
    linkSelected: Boolean = false,
    codeSelected: Boolean = false,
    codeBlockSelected: Boolean = false,
    quoteSelected: Boolean = false,
    bulletListSelected: Boolean = false,
    numberListSelected: Boolean = false,
    alignmentSelected: Int = 0
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarTextButton(text = "↩", contentDescription = "Undo", onClick = onUndoClick, selected = false)
            ToolbarTextButton(text = "↪", contentDescription = "Redo", onClick = onRedoClick, selected = false)

            ToolbarDivider()

            ToolbarTextButton(
                text = "B",
                contentDescription = "Bold",
                onClick = onBoldClick,
                selected = boldSelected,
                fontWeight = FontWeight.Bold,
            )
            ToolbarTextButton(
                text = "I",
                contentDescription = "Italic",
                onClick = onItalicClick,
                selected = italicSelected,
                fontStyle = FontStyle.Italic,
            )
            ToolbarTextButton(
                text = "U",
                contentDescription = "Underline",
                onClick = onUnderlineClick,
                selected = underlineSelected,
                textDecoration = TextDecoration.Underline,
            )
            ToolbarTextButton(
                text = "S",
                contentDescription = "Strikethrough",
                onClick = onStrikethroughClick,
                selected = strikethroughSelected,
                textDecoration = TextDecoration.LineThrough,
            )

            ToolbarDivider()

            ToolbarTextButton(
                text = "H1",
                contentDescription = "Title",
                onClick = onTitleClick,
                selected = titleSelected,
                fontWeight = FontWeight.Bold,
            )
            ToolbarTextButton(
                text = "H2",
                contentDescription = "Subtitle",
                onClick = onSubtitleClick,
                selected = subtitleSelected,
                fontWeight = FontWeight.Bold,
            )
            ToolbarTextButton(
                text = "A",
                contentDescription = "Text Color",
                onClick = onTextColorClick,
                selected = textColorSelected,
                fontWeight = FontWeight.Bold,
            )

            ToolbarDivider()

            ToolbarTextButton(
                text = "🔗",
                contentDescription = "Link",
                onClick = onLinkClick,
                selected = linkSelected,
            )
            ToolbarIconButton(
                iconRes = R.drawable.code,
                contentDescription = "Inline code",
                onClick = onCodeClick,
                selected = codeSelected,
            )
            ToolbarTextButton(
                text = "<>",
                contentDescription = "Code block",
                onClick = onCodeBlockClick,
                selected = codeBlockSelected,
                fontWeight = FontWeight.Bold,
            )
            ToolbarTextButton(
                text = "❝",
                contentDescription = "Quote",
                onClick = onQuoteClick,
                selected = quoteSelected,
            )

            ToolbarDivider()

            ToolbarTextButton(
                text = "•",
                contentDescription = "Bullet List",
                onClick = onBulletListClick,
                selected = bulletListSelected,
                fontWeight = FontWeight.Bold,
            )
            ToolbarTextButton(
                text = "1.",
                contentDescription = "Number List",
                onClick = onNumberListClick,
                selected = numberListSelected,
                fontWeight = FontWeight.Bold,
            )

            ToolbarDivider()

            ToolbarTextButton(
                text = "L",
                contentDescription = "Align Left",
                onClick = onAlignLeftClick,
                selected = alignmentSelected == 0,
            )
            ToolbarTextButton(
                text = "C",
                contentDescription = "Align Center",
                onClick = onAlignCenterClick,
                selected = alignmentSelected == 1,
            )
            ToolbarTextButton(
                text = "R",
                contentDescription = "Align Right",
                onClick = onAlignRightClick,
                selected = alignmentSelected == 2,
            )
        }
    }
}

@Composable
private fun ToolbarTextButton(
    text: String,
    contentDescription: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration? = null,
) {
    ToolbarButtonContainer(
        contentDescription = contentDescription,
        onClick = onClick,
        selected = selected,
        modifier = modifier,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ToolbarIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    ToolbarButtonContainer(
        contentDescription = contentDescription,
        onClick = onClick,
        selected = selected,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ToolbarButtonContainer(
    contentDescription: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) colorResource(R.color.green)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (selected) colorResource(R.color.green)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ToolbarDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 24.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}
