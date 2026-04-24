package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
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
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(viewModel: TransactionViewModel, onAddSupplier: () -> Unit, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Supplier", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSupplier, containerColor = Color(0xFF0984E3)) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Supplier", tint = White)
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (suppliers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada data supplier", color = TextGray)
                    }
                }
            } else {
                items(suppliers) { supplier ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if(isDarkMode) CardBg else White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).background(Color(0xFF0984E3).copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF0984E3))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(supplier.namaSupplier, fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                                Text(supplier.kontak, fontSize = 12.sp, color = TextGray)
                                Text(supplier.alamat, fontSize = 11.sp, color = TextGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
