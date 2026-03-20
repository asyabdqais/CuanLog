package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val categories = listOf(
        CategoryData("Makanan", Icons.Default.Fastfood, Color(0xFFE57373)),
        CategoryData("Transport", Icons.Default.DirectionsCar, Color(0xFF81C784)),
        CategoryData("Hiburan", Icons.Default.Gamepad, Color(0xFF64B5F6)),
        CategoryData("Belanja", Icons.Default.ShoppingBag, Color(0xFFFFB74D)),
        CategoryData("Kesehatan", Icons.Default.MedicalServices, Color(0xFFBA68C8)),
        CategoryData("Tagihan", Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF4DB6AC)),
        CategoryData("Pendidikan", Icons.Default.School, Color(0xFF9575CD)),
        CategoryData("Rumah", Icons.Default.Home, Color(0xFFAED581))
    )

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Kategori Baru", color = White) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nama Kategori", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = White.copy(alpha = 0.2f),
                        focusedBorderColor = PrimaryYellow,
                        unfocusedTextColor = White,
                        focusedTextColor = White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        Toast.makeText(context, "Kategori '$newCategoryName' berhasil ditambahkan (Demo)", Toast.LENGTH_SHORT).show()
                        newCategoryName = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Simpan", color = PrimaryYellow)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = White)
                }
            },
            containerColor = CardBg
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(DashboardBackgroundGradient))
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Kategori", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = PrimaryYellow,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Black)
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Pilih kategori untuk melihat detail atau gunakan tombol + untuk menambah baru.",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { category ->
                        ModernCategoryCard(category) {
                            Toast.makeText(context, "Membuka filter kategori: ${category.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

data class CategoryData(val name: String, val icon: ImageVector, val color: Color)

@Composable
fun ModernCategoryCard(category: CategoryData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(category.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(category.name, color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
