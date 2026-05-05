package com.example.takeawaymonitorapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

val appFontFamily = FontFamily(Font(R.font.your_custom_font))

@Composable
fun Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography(
            defaultFontFamily = appFontFamily
        )
    ) { 
        content() 
    }
}