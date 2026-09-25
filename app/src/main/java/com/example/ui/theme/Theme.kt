package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = NaturalDarkAlt,
    onPrimaryContainer = LimeAccent,
    secondary = LimeAccent,
    onSecondary = NaturalDark,
    background = DarkSurface,
    onBackground = Color.White,
    surface = NaturalDark,
    onSurface = Color.White,
    surfaceVariant = NaturalDarkAlt,
    onSurfaceVariant = SandWarm,
    outline = Slate700,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = SandMuted,
    onPrimaryContainer = NaturalDark,
    secondary = LimeAccent,
    onSecondary = NaturalDark,
    background = NaturalBg,
    onBackground = NaturalDark,
    surface = Color.White,
    onSurface = NaturalDark,
    surfaceVariant = SandMuted,
    onSurfaceVariant = TaupeDark,
    outline = Color(0xFFB8B6AF),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep branded Natural Tones theme consistent
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
