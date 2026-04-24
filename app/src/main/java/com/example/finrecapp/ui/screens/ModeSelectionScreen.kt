package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@Composable
fun ModeSelectionScreen(
    viewModel: TransactionViewModel,
    onSelectFinancial: () -> Unit,
    onSelectCashier: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val bgGradient = if (isDarkMode) LoginBackgroundGradient else listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF))
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(bgGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pilih Layanan",
                style = TextStyle(
                    brush = Brush.horizontalGradient(PurpleGradient),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = "Silakan pilih mode aplikasi yang ingin digunakan",
                color = if (isDarkMode) TextGray else Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModeCard(
                    title = "Personal",
                    subtitle = "Keuangan",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = PrimaryPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onSelectFinancial
                )
                ModeCard(
                    title = "Business",
                    subtitle = "Kasir & Stok",
                    icon = Icons.Default.PointOfSale,
                    color = Color(0xFF00B894),
                    modifier = Modifier.weight(1f),
                    onClick = onSelectCashier
                )
            }
        }
    }
}

@Composable
fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(title, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(subtitle, color = color.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
