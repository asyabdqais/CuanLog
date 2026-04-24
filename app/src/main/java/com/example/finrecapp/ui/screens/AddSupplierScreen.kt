package com.example.finrecapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.data.Supplier
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSupplierScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var nama by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }

    val context = LocalContext.current
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Supplier", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) },
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
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Supplier") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = kontak, onValueChange = { kontak = it }, label = { Text("Kontak / HP") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = alamat, onValueChange = { alamat = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (nama.isNotBlank() && kontak.isNotBlank()) {
                        viewModel.addSupplier(
                            Supplier(namaSupplier = nama, kontak = kontak, alamat = alamat),
                            onSuccess = {
                                Toast.makeText(context, "Supplier berhasil disimpan", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984E3))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Supplier", fontWeight = FontWeight.Bold)
            }
        }
    }
}
