package com.reysl.uroboros.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.utils.MarkdownBlock
import com.reysl.uroboros.utils.MarkdownRenderer
import com.reysl.uroboros.ui.theme.appCodeBlockBackground
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.acherusFeral

@Composable
fun MarkdownPreview(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val accentColor = appGreen()
    val inlineCodeBackground = appCodeBlockBackground()
    val textColor = MaterialTheme.colorScheme.onBackground

    val blocks = remember(markdown, accentColor, inlineCodeBackground, textColor) {
        MarkdownRenderer.parseBlocks(
            markdown = markdown,
            accentColor = accentColor,
            inlineCodeBackground = inlineCodeBackground,
            textColor = textColor,
            fontFamily = acherusFeral,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.RichText -> {
                    if (block.annotated.text.isNotBlank()) {
                        Text(
                            text = block.annotated,
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontFamily = acherusFeral,
                                fontSize = 16.sp,
                                color = textColor,
                                lineHeight = 20.sp,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                            ),
                        )
                    }
                }
                is MarkdownBlock.FencedCode -> {
                    MarkdownCodeBlock(
                        code = block.code,
                        language = block.language,
                    )
                }
            }
        }
    }
}
