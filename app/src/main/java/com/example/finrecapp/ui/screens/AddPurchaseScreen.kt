package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.*
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import com.example.finrecapp.ui.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val products by viewModel.products.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()

    var noFaktur by remember { mutableStateOf("PUR-${System.currentTimeMillis() / 10000}") }
    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }
    var statusPembayaran by remember { mutableStateOf("Lunas") }
    var biayaTambahan by remember { mutableStateOf("") }
    
    val selectedItems = remember { mutableStateListOf<TransactionItem>() }

    val context = LocalContext.current
    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgGradient = if (isDarkMode) DashboardBackgroundGradient else listOf(Color(0xFFF8F9FA), Color(0xFFFFFFFF))

    val totalBarang = selectedItems.sumOf { it.qty * it.hargaSatuan }
    val totalFinal = totalBarang + (biayaTambahan.toLongOrNull() ?: 0L)

    LaunchedEffect(Unit) {
        if (products.isEmpty()) viewModel.fetchProducts()
        if (suppliers.isEmpty()) viewModel.fetchSuppliers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Input Pembelian", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text("Informasi Faktur", color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = noFaktur,
                            onValueChange = { noFaktur = it },
                            label = { Text("No. Faktur") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedSupplier?.namaSupplier ?: "Pilih Supplier",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Penyedia / Supplier") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                if (suppliers.isEmpty()) {
                                    DropdownMenuItem(text = { Text("Daftarkan supplier dulu") }, onClick = {})
                                }
                                suppliers.forEach { supplier ->
                                    DropdownMenuItem(
                                        text = { Text(supplier.namaSupplier) },
                                        onClick = { selectedSupplier = supplier; expanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detail Barang", fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                    Button(
                        onClick = {
                            if (products.isNotEmpty()) {
                                selectedItems.add(TransactionItem(productId = products[0].id, qty = 1, hargaSatuan = products[0].hargaBeliTerakhir, tipe = "Masuk", parentId = 0))
                            } else {
                                Toast.makeText(context, "Daftarkan produk dulu", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Barang")
                    }
                }
            }

            itemsIndexed(selectedItems) { index, item ->
                val product = products.find { it.id == item.productId }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var expandedProd by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.weight(1f)) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().clickable { expandedProd = true },
                                    color = PrimaryPurple.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = product?.namaBarang ?: "Pilih Barang",
                                        modifier = Modifier.padding(12.dp),
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                DropdownMenu(expanded = expandedProd, onDismissRequest = { expandedProd = false }) {
                                    products.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.namaBarang) },
                                            onClick = {
                                                selectedItems[index] = item.copy(productId = p.id, hargaSatuan = p.hargaBeliTerakhir)
                                                expandedProd = false
                                            }
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = { selectedItems.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = if (item.qty == 0) "" else item.qty.toString(),
                                onValueChange = { selectedItems[index] = item.copy(qty = it.toIntOrNull() ?: 0) },
                                label = { Text("QTY") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = formatRupiah(item.hargaSatuan),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Harga Modal") },
                                modifier = Modifier.weight(2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = textColor.copy(alpha = 0.02f)
                                )
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryPurple.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = biayaTambahan,
                            onValueChange = { if (it.all { char -> char.isDigit() }) biayaTambahan = it },
                            label = { Text("Biaya Tambahan (Ongkir/Pajak)") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = ThousandSeparatorTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) }
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text("Total Pembelian", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text(formatRupiah(totalFinal), fontWeight = FontWeight.Black, color = PrimaryPurple, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (selectedSupplier != null && selectedItems.isNotEmpty()) {
                                    val purchase = Purchase(
                                        noFaktur = noFaktur,
                                        tanggal = System.currentTimeMillis(),
                                        supplierId = selectedSupplier!!.id,
                                        totalBiaya = totalBarang,
                                        biayaTambahan = biayaTambahan.toLongOrNull() ?: 0L,
                                        statusPembayaran = statusPembayaran
                                    )
                                    viewModel.addPurchase(purchase, selectedItems.toList())
                                    Toast.makeText(context, "Stok Berhasil Masuk!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan & Masuk Gudang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
