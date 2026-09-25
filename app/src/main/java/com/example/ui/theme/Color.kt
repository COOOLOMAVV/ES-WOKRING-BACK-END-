package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Natural Tones Core Palette (Extracted from Design HTML)
val NaturalBg = Color(0xFFFAF9F6)        // Bone / Warm Canvas
val NaturalDark = Color(0xFF2D332A)      // Deep Olive Charcoal
val NaturalDarkAlt = Color(0xFF384034)   // Dark Forest Charcoal
val ForestGreen = Color(0xFF4A6741)      // Earthy Moss / Signature Brand Green
val ForestGreenDark = Color(0xFF3B5334)  // Deep Forest Green
val ForestGreenLight = Color(0xFFE4EDE1) // Light Forest Tint
val LimeAccent = Color(0xFFB8E297)       // Fresh Meadow Lime Accent
val SandWarm = Color(0xFFE8E6E1)         // Warm Sand / Oatmeal Border
val SandMuted = Color(0xFFF3F1ED)        // Soft Warm Neutral Pill / Container
val TaupeMuted = Color(0xFF63655E)        // Muted Warm Olive Gray / Caption Text (WCAG AA compliant contrast)
val TaupeDark = Color(0xFF5C5A54)        // Subtext Olive Charcoal

val RoseFavorite = Color(0xFFEF4444)     // Red Notification / Heart Accent
val RoseLight = Color(0xFFFEE2E2)
val AmberPending = Color(0xFFD97706)     // Warm Amber
val AmberLight = Color(0xFFFEF3C7)
val WarmAmber = AmberPending

// Backward-compatible semantic color tokens mapped to Natural Tones
val Slate900 = NaturalDark
val Slate800 = NaturalDarkAlt
val Slate700 = Color(0xFF4A5246)
val Slate600 = TaupeDark
val Slate500 = TaupeMuted
val Slate400 = Color(0xFF75736D)
val Slate200 = SandWarm
val Slate100 = SandMuted
val Slate50 = NaturalBg

val NavyPrimary = NaturalDark
val BlueAccent = ForestGreen
val BlueAccentLight = ForestGreenLight
val EmeraldGreen = ForestGreen
val EmeraldLight = ForestGreenLight

val CardBorder = SandWarm
val SurfaceBackground = NaturalBg
val DarkSurface = Color(0xFF1E241B)
