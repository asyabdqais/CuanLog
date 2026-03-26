package com.example.finrecapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TransactionHistoryScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(DashboardBackgroundGradient))
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Riwayat Transaksi", 
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                if (transactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text("Belum ada transaksi", color = TextGray)
                        }
                    }
                } else {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
