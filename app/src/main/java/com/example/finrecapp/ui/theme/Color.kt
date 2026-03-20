package com.example.finrecapp.ui.theme

import androidx.compose.ui.graphics.Color

val DarkBg = Color(0xFF0F111A)
val DarkBgSecondary = Color(0xFF1A1C29)
val CardBg = Color(0xFF1C1E2D)

// Extra Vibrant & Brighter Purple Gradient Theme
val PurpleStart = Color(0xFFFF6EB4) // Lighter & Brighter Pink
val PurpleEnd = Color(0xFFB388FF)   // Lighter & Brighter Purple
val PurpleGradient = listOf(PurpleStart, PurpleEnd)
val PrimaryPurple = PurpleStart

val IncomeGreen = Color(0xFF9CCC65)
val ExpenseRed = Color(0xFFEF5350)
val TextGray = Color(0xFF9496A1)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val GreenGradient = listOf(Color(0xFFD4E157), IncomeGreen)
val RedGradient = listOf(Color(0xFFFFAB91), ExpenseRed)

val MainBackgroundGradient = listOf(DarkBgSecondary, DarkBg)

val LoginBackgroundGradient = listOf(Color(0xFF1A237E), Color(0xFF121420), DarkBg) 
val DashboardBackgroundGradient = listOf(Color(0xFF1A237E), Color(0xFF121420), DarkBg)
val AddTransactionBackgroundGradient = listOf(Color(0xFF242A3D), Color(0xFF121420), DarkBg)

// Maintain aliases for easier migration
val PrimaryYellow = PrimaryPurple
val YellowGradient = PurpleGradient
val PrimaryOrange = PrimaryPurple
val OrangeGradient = PurpleGradient
