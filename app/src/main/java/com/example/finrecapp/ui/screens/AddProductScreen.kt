package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.Product
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import com.example.finrecapp.ui.utils.ThousandSeparatorTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var sku by remember { mutableStateOf("") }
    var namaBarang by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("Umum") }
    var hargaJual by remember { mutableStateOf("") }
    var hargaModal by remember { mutableStateOf("") }

    val context = LocalContext.current
    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgGradient = if (isDarkMode) DashboardBackgroundGradient else listOf(Color(0xFFF8F9FA), Color(0xFFFFFFFF))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Produk Baru", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(brush = Brush.verticalGradient(bgGradient))
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = sku, onValueChange = { sku = it },
                        label = { Text("Kode SKU") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.QrCode, null, tint = PrimaryPurple) },
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = namaBarang, onValueChange = { namaBarang = it },
                        label = { Text("Nama Barang") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Label, null, tint = PrimaryPurple) },
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = hargaModal, 
                            onValueChange = { if (it.all { c -> c.isDigit() }) hargaModal = it },
                            label = { Text("Harga Modal") },
                            modifier = Modifier.weight(1f),
                            visualTransformation = ThousandSeparatorTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) }
                        )
                        OutlinedTextField(
                            value = hargaJual, 
                            onValueChange = { if (it.all { c -> c.isDigit() }) hargaJual = it },
                            label = { Text("Harga Jual") },
                            modifier = Modifier.weight(1f),
                            visualTransformation = ThousandSeparatorTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) }
                        )
                    }
                    
                    OutlinedTextField(
                        value = kategori, onValueChange = { kategori = it },
                        label = { Text("Kategori") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Category, null, tint = PrimaryPurple) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (sku.isNotBlank() && namaBarang.isNotBlank() && hargaModal.isNotBlank() && hargaJual.isNotBlank()) {
                        viewModel.addProduct(
                            Product(
                                sku = sku,
                                namaBarang = namaBarang,
                                kategori = kategori,
                                hargaJual = hargaJual.toLongOrNull() ?: 0L,
                                hargaBeliTerakhir = hargaModal.toLongOrNull() ?: 0L,
                                stok = 0
                            ),
                            onSuccess = {
                                Toast.makeText(context, "Produk berhasil didaftarkan", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Produk", fontWeight = FontWeight.Bold)
            }
        }
    }
}
