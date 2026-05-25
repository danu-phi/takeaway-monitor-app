package com.phsmk.id.takeaway_monitor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RedPH,
    onPrimary = WhiteText,
    background = BlackBackground,
    onBackground = WhiteText,
    surface = DarkSurface,
    onSurface = WhiteText,
    secondary = RedPH,
    onSecondary = WhiteText,
    tertiary = GrayText,
    onTertiary = BlackBackground
)

@Composable
fun TakeawayMonitorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
