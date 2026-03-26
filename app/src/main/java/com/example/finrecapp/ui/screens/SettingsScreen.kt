package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var pushNotifications by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }
    
    // States for "New Page" Dialogs
    var showProfileDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Adaptive Colors
    val bgBrush = if (isDarkMode) {
        Brush.verticalGradient(DashboardBackgroundGradient)
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF8F9FA), Color(0xFFE0E0E0)))
    }
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBackground = if (isDarkMode) CardBg.copy(alpha = 0.5f) else Color.White

    // --- DIALOG: INFORMASI PROFIL ---
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Informasi Profil", fontWeight = FontWeight.Bold, color = textColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = "User FinRec", 
                        onValueChange = {}, 
                        label = { Text("Nama Lengkap") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = PrimaryPurple,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f)
                        )
                    )
                    OutlinedTextField(
                        value = "user@example.com", 
                        onValueChange = {}, 
                        label = { Text("Email") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = PrimaryPurple,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showProfileDialog = false }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Batal", color = textColor) }
            },
            containerColor = if (isDarkMode) CardBg else Color.White
        )
    }

    // --- DIALOG: UBAH KATA SANDI ---
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Ubah Kata Sandi", fontWeight = FontWeight.Bold, color = textColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = "", 
                        onValueChange = {}, 
                        label = { Text("Kata Sandi Lama") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )
                    OutlinedTextField(
                        value = "", 
                        onValueChange = {}, 
                        label = { Text("Kata Sandi Baru") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPasswordDialog = false }) { Text("Perbarui") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("Batal", color = textColor) }
            },
            containerColor = if (isDarkMode) CardBg else Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = bgBrush)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "PENGATURAN",
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(PurpleGradient),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                "cuanlog",
                                style = TextStyle(
                                    color = textColor.copy(alpha = 0.4f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }
                    },
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
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    UserProfileSection(isDarkMode, cardBackground) {
                        showProfileDialog = true
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item { SettingGroupTitle("AKUN & KEAMANAN") }
                item { 
                    SettingActionItem("Informasi Profil", Icons.Default.Person, textColor) {
                        showProfileDialog = true
                    }
                }
                item { 
                    SettingActionItem("Ubah Kata Sandi", Icons.Default.Lock, textColor) {
                        showPasswordDialog = true
                    }
                }
                item { 
                    SettingSwitchItem("Keamanan Biometrik", Icons.Default.Fingerprint, biometricEnabled, textColor, isDarkMode) {
                        biometricEnabled = it
                        Toast.makeText(context, "Biometrik " + (if(it) "Aktif" else "Nonaktif"), Toast.LENGTH_SHORT).show()
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SettingGroupTitle("PREFERENSI") }
                item { 
                    SettingSwitchItem("Notifikasi Push", Icons.Default.Notifications, pushNotifications, textColor, isDarkMode) {
                        pushNotifications = it
                        Toast.makeText(context, "Notifikasi Push Diubah", Toast.LENGTH_SHORT).show()
                    }
                }
                item { 
                    SettingSwitchItem("Mode Gelap", Icons.Default.DarkMode, isDarkMode, textColor, isDarkMode) {
                        viewModel.toggleDarkMode(it)
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item {
                    Button(
                        onClick = { Toast.makeText(context, "Keluar Akun Berhasil", Toast.LENGTH_SHORT).show() },
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
fun UserProfileSection(isDarkMode: Boolean, backgroundColor: Color, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("US", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text("User FinRec", color = if (isDarkMode) Color.White else Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("user@example.com", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingGroupTitle(title: String) {
    Text(
        title, 
        color = PrimaryPurple, 
        fontSize = 11.sp, 
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingActionItem(title: String, icon: ImageVector, textColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = textColor.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingSwitchItem(title: String, icon: ImageVector, checked: Boolean, textColor: Color, isDarkMode: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textColor.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryPurple,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = if (isDarkMode) Color.DarkGray else Color.LightGray
            )
        )
    }
}
