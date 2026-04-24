package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateToInventory: () -> Unit,
    onNavigateToCashier: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onSwitchMode: () -> Unit,
    onLogout: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val report by viewModel.businessReport.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgGradient = if (isDarkMode) DashboardBackgroundGradient else listOf(Color(0xFFF8F9FA), Color(0xFFFFFFFF))

    LaunchedEffect(Unit) {
        viewModel.fetchBusinessReport()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = cardBg,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(PrimaryPurple.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Business Hub", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontSize = 24.sp, fontWeight = FontWeight.Bold))
                    Text("Manajemen Toko Profesional", color = TextGray, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("NAVIGASI", modifier = Modifier.padding(horizontal = 24.dp), color = PrimaryPurple.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                DrawerMenuItem(Icons.Default.Inventory, "Stok Barang", textColor, isDarkMode) { scope.launch { drawerState.close() }; onNavigateToInventory() }
                DrawerMenuItem(Icons.Default.ShoppingCart, "Kasir Penjualan", textColor, isDarkMode) { scope.launch { drawerState.close() }; onNavigateToCashier() }
                DrawerMenuItem(Icons.Default.LocalShipping, "Pembelian Stok", textColor, isDarkMode) { scope.launch { drawerState.close() }; onNavigateToPurchases() }
                DrawerMenuItem(Icons.Default.Business, "Daftar Supplier", textColor, isDarkMode) { scope.launch { drawerState.close() }; onNavigateToSuppliers() }
                DrawerMenuItem(Icons.Default.Assessment, "Laporan Keuangan", textColor, isDarkMode) { scope.launch { drawerState.close() }; onNavigateToReport() }

                Spacer(modifier = Modifier.height(16.dp))
                Text("PENGATURAN", modifier = Modifier.padding(horizontal = 24.dp), color = PrimaryPurple.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                DrawerMenuItem(Icons.Default.SwapHoriz, "Mode Personal", textColor, isDarkMode) { scope.launch { drawerState.close() }; onSwitchMode() }

                Spacer(modifier = Modifier.weight(1f))
                DrawerMenuItem(Icons.AutoMirrored.Filled.Logout, "Keluar", ExpenseRed, isDarkMode) { scope.launch { drawerState.close() }; onLogout() }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.background(cardBg.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textColor)
                    }
                    Text("Business Dashboard", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor))
                    IconButton(
                        onClick = { viewModel.fetchBusinessReport() },
                        modifier = Modifier.background(cardBg.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = textColor)
                    }
                }
            },
            containerColor = Color.Transparent,
            modifier = Modifier.background(brush = Brush.verticalGradient(bgGradient))
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    // Modern Summary Cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCardModern(
                            label = "STOK",
                            value = "${report?.totalStok ?: 0}",
                            unit = "Unit",
                            icon = Icons.Default.Inventory2,
                            color = Color(0xFF0984E3),
                            modifier = Modifier.weight(1f)
                        )
                        StatCardModern(
                            label = "LABA",
                            value = formatRupiah(report?.laba ?: 0L),
                            unit = "Bulan ini",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            color = Color(0xFF00B894),
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }

                item {
                    Text("Layanan Utama", color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MainActionCard("Kasir", "Jual Barang", Icons.Default.PointOfSale, Color(0xFF00B894), Modifier.weight(1f)) { onNavigateToCashier() }
                        MainActionCard("Beli", "Restok Barang", Icons.Default.LocalShipping, PrimaryPurple, Modifier.weight(1f)) { onNavigateToPurchases() }
                    }
                }

                item {
                    Text("Manajemen", color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryActionCard("Inventory & Stok", "Kelola daftar produk Anda", Icons.Default.Inventory, Color(0xFF0984E3)) { onNavigateToInventory() }
                        SecondaryActionCard("Daftar Supplier", "Kelola mitra penyedia barang", Icons.Default.Business, Color(0xFF6C5CE7)) { onNavigateToSuppliers() }
                        SecondaryActionCard("Laporan Bisnis", "Pantau laba dan perkembangan", Icons.Default.Assessment, Color(0xFFE17055)) { onNavigateToReport() }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun StatCardModern(label: String, value: String, unit: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(unit, color = color.copy(alpha = 0.5f), fontSize = 10.sp)
        }
    }
}

@Composable
fun MainActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icon, contentDescription = null, tint = White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = White.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}

@Composable
fun SecondaryActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
                Text(subtitle, color = TextGray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = color.copy(alpha = 0.3f))
        }
    }
}
