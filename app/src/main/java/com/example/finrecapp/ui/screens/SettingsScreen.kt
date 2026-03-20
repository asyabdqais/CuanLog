package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    var pushNotifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(DashboardBackgroundGradient))
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Pengaturan", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    UserProfileSection()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item { SettingGroupTitle("AKUN & KEAMANAN") }
                item { SettingActionItem("Informasi Profil", Icons.Default.Person) }
                item { SettingActionItem("Ubah Kata Sandi", Icons.Default.Lock) }
                item { SettingActionItem("Keamanan Biometrik", Icons.Default.Fingerprint) }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SettingGroupTitle("PREFERENSI") }
                item { 
                    SettingSwitchItem("Notifikasi Push", Icons.Default.Notifications, pushNotifications) {
                        pushNotifications = it
                    }
                }
                item { 
                    SettingSwitchItem("Mode Gelap", Icons.Default.DarkMode, darkMode) {
                        darkMode = it
                    }
                }
                item { SettingActionItem("Mata Uang (IDR)", Icons.Default.Payments) }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SettingGroupTitle("LAINNYA") }
                item { SettingActionItem("Pusat Bantuan", Icons.AutoMirrored.Filled.HelpOutline) }
                item { SettingActionItem("Tentang Aplikasi", Icons.Default.Info) }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { /* Logout */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ExpenseRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Keluar Akun", color = ExpenseRed, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun UserProfileSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("US", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text("User FinRec", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("user@example.com", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { }) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryYellow, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingGroupTitle(title: String) {
    Text(
        title, 
        color = PrimaryYellow, 
        fontSize = 11.sp, 
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingActionItem(title: String, icon: ImageVector) {
    Surface(
        onClick = { },
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = White.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = White, fontSize = 15.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingSwitchItem(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = White.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = White, fontSize = 15.sp)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = PrimaryPurple,
                uncheckedThumbColor = TextGray,
                uncheckedTrackColor = CardBg
            )
        )
    }
}
