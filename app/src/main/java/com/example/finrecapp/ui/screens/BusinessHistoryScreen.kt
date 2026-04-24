package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.Purchase
import com.example.finrecapp.data.Sale
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import com.example.finrecapp.ui.utils.formatRupiah
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessHistoryScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val purchaseHistory by viewModel.purchaseHistory.collectAsState()
    val saleHistory by viewModel.saleHistory.collectAsState()
    val report by viewModel.businessReport.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ringkasan", "Penjualan", "Pembelian")

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)

    LaunchedEffect(Unit) {
        viewModel.fetchBusinessHistory()
        viewModel.fetchBusinessReport()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Laporan & Riwayat", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
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
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryPurple,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Ringkasan Tab (Logic Stok & Laba)
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            SummaryItem("Total Penjualan (Omzet)", formatRupiah(report?.omzet ?: 0L), Color(0xFF00B894))
                            Spacer(modifier = Modifier.height(12.dp))
                            SummaryItem("Harga Pokok Penjualan (HPP)", formatRupiah(report?.hpp ?: 0L), Color(0xFFE17055))
                            Spacer(modifier = Modifier.height(12.dp))
                            SummaryItem("Laba Kotor", formatRupiah(report?.laba ?: 0L), PrimaryPurple)
                            Spacer(modifier = Modifier.height(12.dp))
                            SummaryItem("Stok Akhir", "${report?.totalStok ?: 0} Unit", Color(0xFF0984E3))
                        }
                    }
                }
                1 -> {
                    // Sale History
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (saleHistory.isEmpty()) {
                            item { Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("Belum ada riwayat penjualan", color = TextGray) } }
                        } else {
                            items(saleHistory) { sale -> SaleHistoryItem(sale, isDarkMode) }
                        }
                    }
                }
                2 -> {
                    // Purchase History
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (purchaseHistory.isEmpty()) {
                            item { Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("Belum ada riwayat pembelian", color = TextGray) } }
                        } else {
                            items(purchaseHistory) { purchase -> PurchaseHistoryItem(purchase, isDarkMode) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun SaleHistoryItem(sale: Sale, isDarkMode: Boolean) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDarkMode) CardBg else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF00B894).copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = Color(0xFF00B894))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.noStruk, fontWeight = FontWeight.Bold, color = if(isDarkMode) White else Color.Black)
                Text(sdf.format(Date(sale.tanggal)), fontSize = 12.sp, color = TextGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatRupiah(sale.totalJual), fontWeight = FontWeight.Black, color = Color(0xFF00B894))
                Text("Laba: ${formatRupiah(sale.totalLaba)}", fontSize = 10.sp, color = PrimaryPurple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PurchaseHistoryItem(purchase: Purchase, isDarkMode: Boolean) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDarkMode) CardBg else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(PrimaryPurple.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PrimaryPurple)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(purchase.noFaktur, fontWeight = FontWeight.Bold, color = if(isDarkMode) White else Color.Black)
                Text(sdf.format(Date(purchase.tanggal)), fontSize = 12.sp, color = TextGray)
                Text("Total: ${formatRupiah(purchase.totalBiaya + purchase.biayaTambahan)}", fontSize = 12.sp, color = TextGray)
            }
        }
    }
}
