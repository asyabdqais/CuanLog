package com.example.finrecapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val formattedText = StringBuilder()
        for (i in originalText.indices) {
            formattedText.append(originalText[i])
            val remainingDigits = originalText.length - 1 - i
            if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                formattedText.append(".")
            }
        }

        val annotatedString = AnnotatedString(formattedText.toString())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var dotsBefore = 0
                for (i in 0 until offset) {
                    val remainingDigits = originalText.length - 1 - i
                    if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                        dotsBefore++
                    }
                }
                return offset + dotsBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                var dotsBefore = 0
                var transformedIndex = 0
                for (i in originalText.indices) {
                    transformedIndex++ 
                    if (transformedIndex > offset) break
                    val remainingDigits = originalText.length - 1 - i
                    if (remainingDigits > 0 && remainingDigits % 3 == 0) {
                        transformedIndex++ 
                        dotsBefore++
                        if (transformedIndex > offset) break
                    }
                }
                return offset - dotsBefore
            }
        }

        return TransformedText(annotatedString, offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var jenis by remember { mutableStateOf("Pengeluaran") }
    var nominal by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Lainnya") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

    val categories = if (jenis == "Pengeluaran") {
        listOf("Makanan", "Transportasi", "Hiburan", "Belanja", "Tagihan", "Lainnya")
    } else {
        listOf("Gaji", "Bonus", "Investasi", "Hadiah", "Lainnya")
    }

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBgColor = if (isDarkMode) CardBg else Color.White
    val bgBrush = if (isDarkMode) {
        Brush.verticalGradient(AddTransactionBackgroundGradient)
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))
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
                        Text(
                            "Tambah Transaksi", 
                            style = TextStyle(
                                brush = Brush.horizontalGradient(PurpleGradient),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        ) 
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tipe Transaksi (Switcher)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(if(isDarkMode) CardBg.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(25.dp))
                        .padding(4.dp)
                ) {
                    listOf("Pemasukan", "Pengeluaran").forEach { type ->
                        val isSelected = jenis == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(if (type == "Pemasukan") GreenGradient else RedGradient)
                                    } else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)),
                                    RoundedCornerShape(22.dp)
                                )
                                .clickable { 
                                    jenis = type 
                                    selectedCategory = "Lainnya"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                type,
                                color = if (isSelected) White else (if(isDarkMode) TextGray else Color.Gray),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = jenis,
                    transitionSpec = {
                        if (targetState == "Pengeluaran") {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    },
                    label = "TabTransition"
                ) { targetJenis ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Input Nominal
                        Column {
                            Text("NOMINAL", color = PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = nominal,
                                onValueChange = { if (it.all { char -> char.isDigit() }) nominal = it },
                                prefix = { Text("Rp ", color = textColor, fontWeight = FontWeight.Bold) },
                                textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = ThousandSeparatorTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = textColor.copy(alpha = 0.1f),
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedContainerColor = cardBgColor.copy(alpha = 0.5f),
                                    focusedContainerColor = cardBgColor,
                                    cursorColor = PrimaryPurple
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        // Pilih Kategori
                        Column {
                            Text("KATEGORI", color = PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = textColor.copy(alpha = 0.1f),
                                        focusedBorderColor = PrimaryPurple,
                                        unfocusedContainerColor = cardBgColor.copy(alpha = 0.5f),
                                        focusedContainerColor = cardBgColor,
                                        unfocusedTextColor = textColor,
                                        focusedTextColor = textColor
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(cardBgColor)
                                ) {
                                    val currentCategories = if (targetJenis == "Pengeluaran") {
                                        listOf("Makanan", "Transportasi", "Hiburan", "Belanja", "Tagihan", "Lainnya")
                                    } else {
                                        listOf("Gaji", "Bonus", "Investasi", "Hadiah", "Lainnya")
                                    }
                                    currentCategories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category, color = textColor) },
                                            onClick = {
                                                selectedCategory = category
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Keterangan
                        Column {
                            Text("KETERANGAN", color = PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = keterangan,
                                onValueChange = { keterangan = it },
                                placeholder = { Text("Contoh: Makan siang", color = if(isDarkMode) TextGray else Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = textColor.copy(alpha = 0.1f),
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedContainerColor = cardBgColor.copy(alpha = 0.5f),
                                    focusedContainerColor = cardBgColor,
                                    unfocusedTextColor = textColor,
                                    focusedTextColor = textColor
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        // Pilih Tanggal
                        Column {
                            Text("TANGGAL", color = PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        DatePickerDialog(
                                            context,
                                            if(isDarkMode) android.R.style.Theme_DeviceDefault_Dialog_Alert else android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
                                            { _, year, month, day ->
                                                calendar.set(year, month, day)
                                                selectedDate = calendar.timeInMillis
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = cardBgColor.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sdf.format(Date(selectedDate)), color = textColor, fontWeight = FontWeight.Medium)
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryPurple)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Simpan
                Button(
                    onClick = {
                        val amount = nominal.toLongOrNull() ?: 0L
                        if (amount > 0 && keterangan.isNotBlank()) {
                            viewModel.addTransaction(amount, "$selectedCategory: $keterangan", jenis, selectedDate)
                            Toast.makeText(context, "Transaksi disimpan", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = Brush.horizontalGradient(PurpleGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }
            }
        }
    }
}
