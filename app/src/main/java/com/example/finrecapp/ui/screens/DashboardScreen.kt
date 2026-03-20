package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.NotificationEntity
import com.example.finrecapp.data.TransactionEntity
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel, 
    onAddTransaction: () -> Unit,
    onSeeAllTransactions: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val totalSaldo by viewModel.totalSaldo.collectAsState()
    val totalPemasukan by viewModel.totalPemasukan.collectAsState(initial = 0L)
    val totalPengeluaran by viewModel.totalPengeluaran.collectAsState(initial = 0L)
    val monthlyGrowth by viewModel.monthlyGrowth.collectAsState()
    
    val notifications by viewModel.allNotifications.collectAsState(initial = emptyList())
    val unreadCount by viewModel.unreadNotifCount.collectAsState(initial = 0)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotifSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar Akun", color = White) },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?", color = TextGray) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Keluar", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = White)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardBg,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        "Financial Menu",
                        style = TextStyle(
                            brush = Brush.horizontalGradient(PurpleGradient),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text("Kelola Keuangan Lebih Baik", color = TextGray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                
                DrawerMenuItem(Icons.Default.PieChart, "Analisis Keuangan") { 
                    scope.launch { drawerState.close() } 
                    onNavigateToAnalysis()
                }
                DrawerMenuItem(Icons.Default.Download, "Ekspor Laporan (PDF/CSV)") { 
                    scope.launch { drawerState.close() } 
                    onNavigateToExport()
                }
                DrawerMenuItem(Icons.Default.Category, "Kelola Kategori") { 
                    scope.launch { drawerState.close() } 
                    onNavigateToCategories()
                }
                DrawerMenuItem(Icons.Default.Settings, "Pengaturan Akun") { 
                    scope.launch { drawerState.close() } 
                    onNavigateToSettings()
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                DrawerMenuItem(Icons.AutoMirrored.Filled.Logout, "Keluar", textColor = ExpenseRed) { 
                    scope.launch { drawerState.close() } 
                    showLogoutDialog = true
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(DashboardBackgroundGradient))
        ) {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = White)
                        }
                        Text(
                            text = "Financial Dashboard", 
                            style = TextStyle(
                                brush = Brush.horizontalGradient(PurpleGradient),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        IconButton(onClick = { 
                            showNotifSheet = true
                            viewModel.markNotificationsAsRead()
                        }) {
                            BadgedBox(
                                badge = { 
                                    if (unreadCount > 0) {
                                        Badge(containerColor = PrimaryPurple) { 
                                            Text(unreadCount.toString(), color = White) 
                                        } 
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = White)
                            }
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddTransaction,
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    brush = Brush.linearGradient(YellowGradient),
                                    shape = FloatingActionButtonDefaults.shape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = White)
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SaldoCard(totalSaldo, monthlyGrowth)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard("PEMASUKAN", totalPemasukan, GreenGradient, Icons.Default.ArrowUpward, Modifier.weight(1f))
                            StatCard("PENGELUARAN", totalPengeluaran, RedGradient, Icons.Default.ArrowDownward, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Riwayat Transaksi", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                "Lihat Semua", 
                                color = PrimaryYellow, 
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { onSeeAllTransactions() }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Show only first 5 items on dashboard
                    val recentTransactions = transactions.take(5)
                    if (recentTransactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada riwayat transaksi", color = TextGray)
                            }
                        }
                    } else {
                        items(recentTransactions) { transaction ->
                            TransactionItem(transaction)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    // Notification Bottom Sheet
    if (showNotifSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotifSheet = false },
            sheetState = sheetState,
            containerColor = CardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text("Notifikasi Terbaru", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                
                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Tidak ada notifikasi", color = TextGray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                        items(notifications) { notif ->
                            NotificationItem(notif.title, notif.message)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DrawerMenuItem(icon: ImageVector, label: String, textColor: Color = White, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = if(textColor == White) PrimaryPurple else textColor) },
        label = { Text(label, color = textColor, fontWeight = FontWeight.Medium) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun NotificationItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryPurple.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SaldoCard(saldo: Long, growthText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF8A65), Color(0xFF9575CD))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text("TOTAL SALDO", color = White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(formatRupiah(saldo), color = White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        growthText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, amount: Long, gradient: List<Color>, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradient.map { it.copy(alpha = 0.8f) }))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(label, color = Black.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(formatRupiah(amount), color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val isIncome = transaction.jenis == "Pemasukan"
    val sdf = SimpleDateFormat("dd", Locale.getDefault())
    val monthSdf = SimpleDateFormat("MMM", Locale.getDefault())
    val date = Date(transaction.tanggal)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Centered Date Circle
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = monthSdf.format(date).uppercase(), 
                    color = TextGray, 
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Text(
                    text = sdf.format(date), 
                    color = White, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.keterangan, color = White, fontWeight = FontWeight.Bold)
            Text(if (isIncome) "Transfer Masuk" else "Tagihan", color = TextGray, fontSize = 12.sp)
        }
        
        Text(
            text = (if (isIncome) "+" else "-") + formatRupiah(transaction.nominal),
            color = if (isIncome) IncomeGreen else ExpenseRed,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}
