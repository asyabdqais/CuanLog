package com.example.finrecapp.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.finrecapp.data.TransactionEntity
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedRange by remember { mutableStateOf("Semua") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(DashboardBackgroundGradient))
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Ekspor Laporan", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pilih Rentang Waktu", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RangeChip("Bulan Ini", selectedRange == "Bulan Ini") { selectedRange = "Bulan Ini" }
                        RangeChip("Semua", selectedRange == "Semua") { selectedRange = "Semua" }
                    }
                }

                item {
                    Text("Format Dokumen", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ExportCard(
                        title = "Laporan Ringkas (PDF)",
                        desc = "Ringkasan saldo dan transaksi.",
                        icon = Icons.Default.PictureAsPdf,
                        color = Color(0xFFEF5350)
                    ) {
                        Toast.makeText(context, "Fitur PDF memerlukan library tambahan. Mengalihkan ke CSV...", Toast.LENGTH_LONG).show()
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ExportCard(
                        title = "Spreadsheet Lengkap (CSV)",
                        desc = "Data transaksi untuk Excel.",
                        icon = Icons.Default.TableChart,
                        color = Color(0xFF66BB6A)
                    ) {
                        scope.launch {
                            val transactions = viewModel.allTransactions.first()
                            exportToCsv(context, transactions)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryPurple.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryPurple)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "File CSV akan disimpan di folder dokumen dan dapat langsung dibuka di Excel/Sheets.",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

fun exportToCsv(context: Context, transactions: List<TransactionEntity>) {
    val fileName = "Laporan_Keuangan_${System.currentTimeMillis()}.csv"
    val csvHeader = "ID,Tanggal,Keterangan,Jenis,Nominal\n"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    val csvData = StringBuilder()
    csvData.append(csvHeader)
    
    transactions.forEach {
        val date = sdf.format(Date(it.tanggal))
        csvData.append("${it.id},$date,${it.keterangan},${it.jenis},${it.nominal}\n")
    }

    try {
        val file = File(context.getExternalFilesDir(null), fileName)
        val out = FileOutputStream(file)
        out.write(csvData.toString().toByteArray())
        out.close()

        val path: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan FinRec")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, path)
        context.startActivity(Intent.createChooser(intent, "Bagikan Laporan"))
        
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal mengekspor: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun RangeChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryPurple else CardBg.copy(alpha = 0.5f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.1f))
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) White else TextGray,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ExportCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, color = TextGray, fontSize = 12.sp)
            }
            Icon(Icons.Default.Download, contentDescription = null, tint = White.copy(alpha = 0.3f))
        }
    }
}
