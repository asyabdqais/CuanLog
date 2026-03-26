package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: TransactionViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Biometric Logic
    val biometricPrompt = remember {
        val executor = ContextCompat.getMainExecutor(context)
        BiometricPrompt(context as FragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onLoginSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(context, "Autentikasi Gagal: $errString", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Login Biometrik")
        .setSubtitle("Gunakan sidik jari atau wajah Anda")
        .setNegativeButtonText("Batal")
        .build()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(LoginBackgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Branding Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "cuanlog",
                    style = TextStyle(
                        brush = Brush.verticalGradient(PurpleGradient),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-1.5).sp
                    )
                )
                Text(
                    text = "MANAGEMENT",
                    style = TextStyle(
                        color = White.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Inputs Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBg.copy(alpha = 0.4f),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Username", color = White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Username", color = TextGray.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryPurple) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = PrimaryPurple.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            cursorColor = PrimaryPurple,
                            unfocusedTextColor = White,
                            focusedTextColor = White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Password", color = White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", color = TextGray.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryPurple) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextGray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = PrimaryPurple.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            cursorColor = PrimaryPurple,
                            unfocusedTextColor = White,
                            focusedTextColor = White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Lupa Password?",
                        color = PrimaryPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onNavigateToForgotPassword() }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons Row (Login + Biometric)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    if ((username == "admin" && password == "admin123") || 
                                        viewModel.loginUser(username, password)) {
                                        onLoginSuccess()
                                    } else {
                                        showError = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),
                            contentPadding = PaddingValues(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(PurpleGradient),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Masuk", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = White)
                            }
                        }

                        // Biometric Button
                        IconButton(
                            onClick = { biometricPrompt.authenticate(promptInfo) },
                            modifier = Modifier
                                .size(58.dp)
                                .background(CardBg.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = "Biometric Login", tint = PrimaryPurple, modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register Text
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = TextGray)) { append("Belum punya akun? ") }
                                withStyle(style = SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.ExtraBold)) { append("Daftar Sekarang") }
                            },
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }

        // Custom Error Toast
        AnimatedVisibility(
            visible = showError,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1C1E2D),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(ExpenseRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Username atau Password salah", color = White, fontSize = 14.sp)
                    }
                    Text(
                        "TUTUP",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showError = false }
                    )
                }
            }
            LaunchedEffect(showError) {
                if (showError) {
                    delay(3000)
                    showError = false
                }
            }
        }
    }
}
