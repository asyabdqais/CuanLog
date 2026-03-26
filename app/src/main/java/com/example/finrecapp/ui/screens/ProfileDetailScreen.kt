package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current
    
    var fullName by remember { mutableStateOf("User FinRec") }
    var email by remember { mutableStateOf("user@example.com") }
    var phone by remember { mutableStateOf("08123456789") }

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgBrush = if (isDarkMode) Brush.verticalGradient(DashboardBackgroundGradient) else Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))

    Box(modifier = Modifier.fillMaxSize().background(brush = bgBrush)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Informasi Profil", fontWeight = FontWeight.Bold, color = textColor) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = textColor, contentDescription = "Back") } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Picture Area
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(100.dp).background(PrimaryPurple, CircleShape), contentAlignment = Alignment.Center) {
                        Text("US", color = White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(textColor, CircleShape)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = if(isDarkMode) Black else White, modifier = Modifier.size(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ProfileEditItem("Nama Lengkap", fullName, textColor, cardBg) { fullName = it }
                ProfileEditItem("Email", email, textColor, cardBg) { email = it }
                ProfileEditItem("Nomor Telepon", phone, textColor, cardBg) { phone = it }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { Toast.makeText(context, "Profil Berhasil Disimpan", Toast.LENGTH_SHORT).show(); onBack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileEditItem(label: String, value: String, textColor: Color, cardBg: Color, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, color = PrimaryPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = textColor.copy(alpha = 0.1f),
                focusedBorderColor = PrimaryPurple,
                unfocusedContainerColor = cardBg,
                focusedContainerColor = cardBg,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )
    }
}
