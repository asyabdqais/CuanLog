package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgBrush = if (isDarkMode) {
        Brush.verticalGradient(DashboardBackgroundGradient)
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))
    }

    val categories = listOf(
        CategoryItem("Makanan", Icons.Default.Restaurant, Color(0xFFE57373)),
        CategoryItem("Transport", Icons.Default.DirectionsCar, Color(0xFF81C784)),
        CategoryItem("Hiburan", Icons.Default.Gamepad, Color(0xFF64B5F6)),
        CategoryItem("Belanja", Icons.Default.ShoppingBag, Color(0xFFFFB74D)),
        CategoryItem("Tagihan", Icons.Default.Receipt, Color(0xFF4DB6AC)),
        CategoryItem("Gaji", Icons.Default.Payments, Color(0xFF9575CD)),
        CategoryItem("Kesehatan", Icons.Default.MedicalServices, Color(0xFFF06292)),
        CategoryItem("Investasi", Icons.Default.TrendingUp, Color(0xFFD4E157))
    )

    Box(modifier = Modifier.fillMaxSize().background(brush = bgBrush)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Kategori Keuangan", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category, cardBg, textColor, isDarkMode)
                }
            }
        }
    }
}

data class CategoryItem(val name: String, val icon: ImageVector, val color: Color)

@Composable
fun CategoryCard(item: CategoryItem, bgColor: Color, textColor: Color, isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if(isDarkMode) 0.dp else 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(50.dp).background(item.color.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.name, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
