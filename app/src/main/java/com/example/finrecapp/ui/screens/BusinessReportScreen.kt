package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.finrecapp.ui.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessReportScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val report by viewModel.businessReport.collectAsState()

    // Refresh report data when screen is opened
    LaunchedEffect(Unit) {
        viewModel.fetchBusinessReport()
    }

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Laporan Bisnis", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ReportCard(
                    title = "Total Omzet",
                    value = formatRupiah(report?.omzet ?: 0L),
                    icon = Icons.Default.MonetizationOn,
                    color = Color(0xFF00B894)
                )
            }
            item {
                ReportCard(
                    title = "Laba Kotor",
                    value = formatRupiah(report?.laba ?: 0L),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = PrimaryPurple
                )
            }
            item {
                ReportCard(
                    title = "Total Stok Barang",
                    value = "${report?.totalStok ?: 0} Unit",
                    icon = Icons.Default.Inventory,
                    color = Color(0xFF0984E3)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Analisis Singkat", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if(isDarkMode) CardBg else White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val analysisText = if ((report?.laba ?: 0) > 0) {
                            "Bisnis Anda berjalan baik dengan laba ${formatRupiah(report?.laba ?: 0)}. Tingkatkan terus penjualannya!"
                        } else {
                            "Belum ada data laba atau omzet yang tercatat. Silakan lakukan transaksi penjualan terlebih dahulu."
                        }
                        Text(
                            text = analysisText,
                            color = if(isDarkMode) TextGray else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier.size(56.dp).background(color.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }
        }
    }
}
