package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current
    
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgBrush = if (isDarkMode) Brush.verticalGradient(DashboardBackgroundGradient) else Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))

    Box(modifier = Modifier.fillMaxSize().background(brush = bgBrush)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Ubah Kata Sandi", fontWeight = FontWeight.Bold, color = textColor) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = textColor, contentDescription = "Back") } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(24.dp)) {
                Text("Pastikan kata sandi baru Anda kuat dan sulit ditebak.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                
                PasswordInputField("Kata Sandi Lama", oldPassword, oldVisible, textColor, cardBg, { oldPassword = it }, { oldVisible = !oldVisible })
                Spacer(modifier = Modifier.height(16.dp))
                PasswordInputField("Kata Sandi Baru", newPassword, newVisible, textColor, cardBg, { newPassword = it }, { newVisible = !newVisible })
                Spacer(modifier = Modifier.height(16.dp))
                PasswordInputField("Konfirmasi Kata Sandi Baru", confirmPassword, newVisible, textColor, cardBg, { confirmPassword = it }, { newVisible = !newVisible })
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { 
                        if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                            Toast.makeText(context, "Kata Sandi Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, "Konfirmasi Kata Sandi Tidak Cocok", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Perbarui Kata Sandi", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PasswordInputField(label: String, value: String, visible: Boolean, textColor: Color, cardBg: Color, onValueChange: (String) -> Unit, onToggleVisible: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = PrimaryPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisible) {
                    Icon(if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextGray)
                }
            },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryPurple) },
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
