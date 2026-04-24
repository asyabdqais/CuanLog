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
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.finrecapp.data.*
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import com.example.finrecapp.ui.utils.formatRupiah
import com.example.finrecapp.ui.utils.ThousandSeparatorTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val products by viewModel.products.collectAsState()

    var noStruk by remember { mutableStateOf("SLS-${System.currentTimeMillis() / 10000}") }
    var pelangganNama by remember { mutableStateOf("") }
    var metodePembayaran by remember { mutableStateOf("Tunai") }
    var kasirNama by remember { mutableStateOf("Admin") }
    
    val selectedItems = remember { mutableStateListOf<TransactionItem>() }

    val context = LocalContext.current
    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgGradient = if (isDarkMode) DashboardBackgroundGradient else listOf(Color(0xFFF8F9FA), Color(0xFFFFFFFF))

    // Hitung Total & Laba
    val totalJual = selectedItems.sumOf { it.qty * it.hargaSatuan }
    val totalLaba = selectedItems.sumOf { item ->
        val product = products.find { it.id == item.productId }
        val modal = product?.hargaBeliTerakhir ?: 0L
        (item.hargaSatuan - modal) * item.qty
    }

    LaunchedEffect(Unit) {
        if (products.isEmpty()) viewModel.fetchProducts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kasir Penjualan", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
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
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // --- Info Transaksi ---
            item {
                Text("Informasi Transaksi", color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = noStruk,
                            onValueChange = { noStruk = it },
                            label = { Text("Nomor Struk") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = textColor.copy(alpha = 0.1f))
                        )
                        OutlinedTextField(
                            value = pelangganNama,
                            onValueChange = { pelangganNama = it },
                            label = { Text("Nama Pelanggan") },
                            placeholder = { Text("Opsional") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = textColor.copy(alpha = 0.1f))
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = metodePembayaran,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Metode Pembayaran") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = textColor.copy(alpha = 0.1f))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listOf("Tunai", "QRIS", "Transfer Bank", "Debit").forEach { method ->
                                    DropdownMenuItem(text = { Text(method) }, onClick = { metodePembayaran = method; expanded = false })
                                }
                            }
                        }
                    }
                }
            }

            // --- Keranjang ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Keranjang Belanja", fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                    Button(
                        onClick = {
                            if (products.isNotEmpty()) {
                                val firstAvailable = products.find { it.stok > 0 } ?: products[0]
                                selectedItems.add(TransactionItem(productId = firstAvailable.id, qty = if(firstAvailable.stok > 0) 1 else 0, hargaSatuan = firstAvailable.hargaJual, tipe = "Keluar", parentId = 0))
                            } else {
                                Toast.makeText(context, "Daftarkan produk dulu di menu Inventory", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Produk")
                    }
                }
            }

            if (selectedItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextGray.copy(alpha = 0.3f))
                            Text("Keranjang masih kosong", color = TextGray)
                        }
                    }
                }
            } else {
                itemsIndexed(selectedItems) { index, item ->
                    val product = products.find { it.id == item.productId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00B894).copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                var expandedProd by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().clickable { expandedProd = true },
                                        color = Color(0xFF00B894).copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = product?.namaBarang ?: "Pilih Produk",
                                            modifier = Modifier.padding(12.dp),
                                            color = textColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    DropdownMenu(expanded = expandedProd, onDismissRequest = { expandedProd = false }) {
                                        products.forEach { p ->
                                            DropdownMenuItem(
                                                text = { 
                                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                        Text(p.namaBarang)
                                                        Text("Stok: ${p.stok}", color = if(p.stok > 0) Color(0xFF00B894) else ExpenseRed, fontWeight = FontWeight.Bold)
                                                    }
                                                },
                                                onClick = {
                                                    selectedItems[index] = item.copy(productId = p.id, hargaSatuan = p.hargaJual, qty = if(p.stok > 0) 1 else 0)
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
                                    onValueChange = { 
                                        val newQty = it.toIntOrNull() ?: 0
                                        val stock = product?.stok ?: 0
                                        if (newQty <= stock) {
                                            selectedItems[index] = item.copy(qty = newQty)
                                        } else {
                                            Toast.makeText(context, "Stok hanya tersedia $stock unit!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    label = { Text("QTY") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = formatRupiah(item.hargaSatuan),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Harga Jual") },
                                    modifier = Modifier.weight(2f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = textColor.copy(alpha = 0.02f)
                                    )
                                )
                            }
                            if ((product?.stok ?: 0) <= 0) {
                                Text("Stok Habis! Tidak bisa dijual.", color = ExpenseRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- Ringkasan Pembayaran ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00B894).copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00B894).copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text("Total Pembayaran", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text(formatRupiah(totalJual), fontWeight = FontWeight.Black, color = Color(0xFF00B894), fontSize = 24.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimasi Keuntungan", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                            Text(formatRupiah(totalLaba), fontWeight = FontWeight.Bold, color = PrimaryPurple, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isAnyOutOfStock = selectedItems.any { it.qty <= 0 || it.qty > (products.find { p -> p.id == it.productId }?.stok ?: 0) }

                        Button(
                            onClick = {
                                if (selectedItems.isNotEmpty() && !isAnyOutOfStock) {
                                    val sale = Sale(
                                        noStruk = noStruk,
                                        tanggal = System.currentTimeMillis(),
                                        metodePembayaran = metodePembayaran,
                                        pelangganNama = pelangganNama,
                                        kasirNama = kasirNama,
                                        totalJual = totalJual,
                                        totalLaba = totalLaba
                                    )
                                    viewModel.addSale(sale, selectedItems.toList())
                                    Toast.makeText(context, "Penjualan Berhasil!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else if (isAnyOutOfStock) {
                                    Toast.makeText(context, "Ada item yang stoknya tidak mencukupi!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = selectedItems.isNotEmpty() && !isAnyOutOfStock,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894))
                        ) {
                            Icon(Icons.Default.PointOfSale, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Selesaikan Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
