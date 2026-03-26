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

// Pastel Modern Gradients
val PastelIncomeGradient = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA)) // Soft Cyan/Blue
val PastelExpenseGradient = listOf(Color(0xFFFFF1F1), Color(0xFFFFE4E1), Color(0xFFFFC0CB)) // Soft Pink/Rose
val PastelPurpleGradient = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFCE93D8)) // Soft Purple

val MainBackgroundGradient = listOf(DarkBgSecondary, DarkBg)

val LoginBackgroundGradient = listOf(Color(0xFF1A237E), Color(0xFF121420), DarkBg) 
val DashboardBackgroundGradient = listOf(Color(0xFF1A237E), Color(0xFF121420), DarkBg)
val AddTransactionBackgroundGradient = listOf(Color(0xFF242A3D), Color(0xFF121420), DarkBg)

// Maintain aliases for easier migration
val PrimaryYellow = PrimaryPurple
val YellowGradient = PurpleGradient
val PrimaryOrange = PrimaryPurple
val OrangeGradient = PurpleGradient
