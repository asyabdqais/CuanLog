package com.example.finrecapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(AddTransactionBackgroundGradient))
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Tambah Transaksi", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tipe Transaksi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(CardBg.copy(alpha = 0.5f), RoundedCornerShape(25.dp))
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
                                color = if (isSelected) White else TextGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Input Nominal
                Column {
                    Text("NOMINAL", color = PrimaryYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nominal,
                        onValueChange = { if (it.all { char -> char.isDigit() }) nominal = it },
                        prefix = { Text("Rp ", color = White, fontWeight = FontWeight.Bold) },
                        textStyle = LocalTextStyle.current.copy(color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = White.copy(alpha = 0.1f),
                            focusedBorderColor = PrimaryYellow,
                            unfocusedContainerColor = CardBg.copy(alpha = 0.3f),
                            focusedContainerColor = CardBg.copy(alpha = 0.5f),
                            cursorColor = PrimaryYellow
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Pilih Kategori
                Column {
                    Text("KATEGORI", color = PrimaryYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                                unfocusedBorderColor = White.copy(alpha = 0.1f),
                                focusedBorderColor = PrimaryYellow,
                                unfocusedContainerColor = CardBg.copy(alpha = 0.3f),
                                focusedContainerColor = CardBg.copy(alpha = 0.5f),
                                unfocusedTextColor = White,
                                focusedTextColor = White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardBg)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = White) },
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
                    Text("KETERANGAN", color = PrimaryYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        placeholder = { Text("Contoh: Makan siang di kantor", color = TextGray.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = White.copy(alpha = 0.1f),
                            focusedBorderColor = PrimaryYellow,
                            unfocusedContainerColor = CardBg.copy(alpha = 0.3f),
                            focusedContainerColor = CardBg.copy(alpha = 0.5f),
                            unfocusedTextColor = White,
                            focusedTextColor = White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Pilih Tanggal
                Column {
                    Text("TANGGAL", color = PrimaryYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                DatePickerDialog(
                                    context,
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
                        color = CardBg.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sdf.format(Date(selectedDate)), color = White, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryYellow)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Simpan
                Button(
                    onClick = {
                        val amount = nominal.toLongOrNull() ?: 0L
                        if (amount <= 0) {
                            Toast.makeText(context, "Masukkan nominal yang valid", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (keterangan.isBlank()) {
                            Toast.makeText(context, "Keterangan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        viewModel.addTransaction(amount, "$selectedCategory: $keterangan", jenis, selectedDate)
                        Toast.makeText(context, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = Brush.horizontalGradient(YellowGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Black)
                        }
                    }
                }
            }
        }
    }
}
