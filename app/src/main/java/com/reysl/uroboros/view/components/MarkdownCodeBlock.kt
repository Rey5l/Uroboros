package com.reysl.uroboros.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.ui.theme.appCodeBlockBackground
import com.reysl.uroboros.ui.theme.appCodeBlockLanguage
import com.reysl.uroboros.ui.theme.appCodeBlockText

@Composable
fun MarkdownCodeBlock(
    code: String,
    language: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(appCodeBlockBackground())
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (!language.isNullOrBlank()) {
            Text(
                text = language,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = appCodeBlockLanguage(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Text(
            text = code.ifBlank { " " },
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = appCodeBlockText(),
        )
    }
}
