package com.example.finrecapp.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.finrecapp.data.TransactionEntity
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val context = LocalContext.current
    
    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBg = if (isDarkMode) CardBg else Color.White
    val bgBrush = if (isDarkMode) {
        Brush.verticalGradient(DashboardBackgroundGradient)
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = bgBrush)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Ekspor Laporan", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Pilih Format Laporan", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                ExportOptionCard("Dokumen PDF (.pdf)", "Laporan lengkap dengan grafik", Icons.Default.PictureAsPdf, Color(0xFFEF5350), cardBg, textColor, isDarkMode) {
                    if (transactions.isNotEmpty()) {
                        exportAndOpenFile(context, transactions, "FinRec_Report.csv")
                    } else {
                        Toast.makeText(context, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
                    }
                }
                
                ExportOptionCard("Data Excel (.csv)", "Data mentah untuk pengolahan lanjut", Icons.Default.Description, Color(0xFF66BB6A), cardBg, textColor, isDarkMode) {
                    if (transactions.isNotEmpty()) {
                        exportAndOpenFile(context, transactions, "FinRec_Report.csv")
                    } else {
                        Toast.makeText(context, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { shareReport(context) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bagikan Laporan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun exportAndOpenFile(context: Context, transactions: List<TransactionEntity>, fileName: String) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val stringBuilder = StringBuilder()
    stringBuilder.append("Tanggal,Keterangan,Kategori,Jenis,Nominal\n")
    
    for (transaction in transactions) {
        val date = sdf.format(Date(transaction.tanggal))
        stringBuilder.append("$date,${transaction.keterangan},${transaction.kategori},${transaction.jenis},${transaction.nominal}\n")
    }

    try {
        val folder = File(context.cacheDir, "reports")
        if (!folder.exists()) folder.mkdirs()
        
        val file = File(folder, fileName)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(stringBuilder.toString().toByteArray())
        fileOutputStream.close()
        
        // Membuka file secara otomatis
        val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        context.startActivity(Intent.createChooser(intent, "Buka Laporan"))
        Toast.makeText(context, "Laporan berhasil dibuat", Toast.LENGTH_SHORT).show()
        
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal membuka laporan", Toast.LENGTH_SHORT).show()
    }
}

fun shareReport(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Laporan FinRec")
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Berikut adalah laporan keuangan saya.")
    context.startActivity(Intent.createChooser(shareIntent, "Bagikan"))
}

@Composable
fun ExportOptionCard(title: String, desc: String, icon: ImageVector, iconColor: Color, bgColor: Color, textColor: Color, isDarkMode: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if(isDarkMode) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(iconColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, color = TextGray, fontSize = 12.sp)
            }
        }
    }
}
