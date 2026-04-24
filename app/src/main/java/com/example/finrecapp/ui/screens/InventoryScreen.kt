package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.Product
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import com.example.finrecapp.ui.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: TransactionViewModel, onAddProduct: () -> Unit, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter { 
        it.namaBarang.contains(searchQuery, ignoreCase = true) || 
        it.sku.contains(searchQuery, ignoreCase = true) 
    }

    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgGradient = if (isDarkMode) DashboardBackgroundGradient else listOf(Color(0xFFF8F9FA), Color(0xFFFFFFFF))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stok Barang", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchProducts() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Refresh", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct, 
                containerColor = PrimaryPurple,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang", tint = White)
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(brush = Brush.verticalGradient(bgGradient))
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 20.dp)) {
            // Search Bar Modern
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari SKU atau Nama Barang...", color = TextGray.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPurple) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = cardBg.copy(alpha = 0.5f),
                    focusedContainerColor = cardBg,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryPurple.copy(alpha = 0.5f),
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(80.dp).background(TextGray.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(40.dp), tint = TextGray.copy(alpha = 0.3f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gudang Kosong", color = TextGray, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductItemCardModern(product, isDarkMode)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCardModern(product: Product, isDarkMode: Boolean) {
    val cardBg = if (isDarkMode) CardBg else Color.White
    val textColor = if (isDarkMode) White else Color(0xFF1A1C1E)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if(isDarkMode) White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.02f))
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).background(PrimaryPurple.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.namaBarang, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Text("SKU: ${product.sku}", fontSize = 12.sp, color = TextGray)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(color = PrimaryPurple.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(product.kategori, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = PrimaryPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tersedia", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                Text(
                    "${product.stok}", 
                    fontWeight = FontWeight.Black, 
                    fontSize = 22.sp, 
                    color = if (product.stok < 10) ExpenseRed else Color(0xFF00B894)
                )
                Text(formatRupiah(product.hargaJual), fontSize = 12.sp, color = textColor.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
            }
        }
    }
}
