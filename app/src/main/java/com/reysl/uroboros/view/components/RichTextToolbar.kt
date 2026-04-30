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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
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
            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Undo",
                onClick = onUndoClick,
                selected = false
            )

            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.Redo,
                contentDescription = "Redo",
                onClick = onRedoClick,
                selected = false
            )

            ToolbarDivider()

            ToolbarButton(
                icon = Icons.Default.FormatBold,
                contentDescription = "Bold",
                onClick = onBoldClick,
                selected = boldSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatItalic,
                contentDescription = "Italic",
                onClick = onItalicClick,
                selected = italicSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatUnderlined,
                contentDescription = "Underline",
                onClick = onUnderlineClick,
                selected = underlineSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatStrikethrough,
                contentDescription = "Strikethrough",
                onClick = onStrikethroughClick,
                selected = strikethroughSelected
            )

            ToolbarDivider()

            ToolbarButton(
                icon = Icons.Default.Title,
                contentDescription = "Title",
                onClick = onTitleClick,
                selected = titleSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatSize,
                contentDescription = "Subtitle",
                onClick = onSubtitleClick,
                selected = subtitleSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatColorText,
                contentDescription = "Text Color",
                onClick = onTextColorClick,
                selected = textColorSelected
            )

            ToolbarDivider()

            ToolbarButton(
                icon = Icons.Default.AddLink,
                contentDescription = "Link",
                onClick = onLinkClick,
                selected = linkSelected
            )

            ToolbarButton(
                icon = Icons.Default.Code,
                contentDescription = "Code",
                onClick = onCodeClick,
                selected = codeSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatQuote,
                contentDescription = "Quote",
                onClick = onQuoteClick,
                selected = quoteSelected
            )

            ToolbarDivider()

            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Bullet List",
                onClick = onBulletListClick,
                selected = bulletListSelected
            )

            ToolbarButton(
                icon = Icons.Default.FormatListNumbered,
                contentDescription = "Number List",
                onClick = onNumberListClick,
                selected = numberListSelected
            )

            ToolbarDivider()

            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                contentDescription = "Align Left",
                onClick = onAlignLeftClick,
                selected = alignmentSelected == 0
            )

            ToolbarButton(
                icon = Icons.Default.FormatAlignCenter,
                contentDescription = "Align Center",
                onClick = onAlignCenterClick,
                selected = alignmentSelected == 1
            )

            ToolbarButton(
                icon = Icons.AutoMirrored.Filled.FormatAlignRight,
                contentDescription = "Align Right",
                onClick = onAlignRightClick,
                selected = alignmentSelected == 2
            )
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
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
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
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
