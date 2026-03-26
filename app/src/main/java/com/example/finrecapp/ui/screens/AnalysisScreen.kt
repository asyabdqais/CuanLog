package com.example.finrecapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finrecapp.ui.TransactionViewModel
import com.example.finrecapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(viewModel: TransactionViewModel, onBack: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val totalPemasukan by viewModel.totalPemasukan.collectAsState(initial = 0L)
    val totalPengeluaran by viewModel.totalPengeluaran.collectAsState(initial = 0L)
    val totalBalance by viewModel.totalSaldo.collectAsState()

    val categoryTotals = transactions
        .filter { it.jenis == "Pengeluaran" }
        .groupBy { it.keterangan.split(":").firstOrNull() ?: "Lainnya" }
        .mapValues { it.value.sumOf { it.nominal } }

    val textColor = if (isDarkMode) White else Color(0xFF2D3436)
    val cardBg = if (isDarkMode) CardBg.copy(alpha = 0.6f) else Color.White
    val bgBrush = if (isDarkMode) {
        Brush.verticalGradient(DashboardBackgroundGradient)
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F2F5), Color(0xFFFFFFFF)))
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = bgBrush)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text("Analisis Keuangan", style = TextStyle(brush = Brush.horizontalGradient(PurpleGradient), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor) }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 20.dp)) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FinancialOverviewCard(totalPemasukan, totalPengeluaran, isDarkMode, cardBg, textColor)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Detail Performa", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PerformanceItem("Efisiensi", if (totalPemasukan > 0) "${((totalBalance.toFloat() / totalPemasukan) * 100).toInt()}%" else "0%", Icons.AutoMirrored.Filled.TrendingUp, IncomeGreen, isDarkMode, cardBg, textColor, Modifier.weight(1f))
                        PerformanceItem("Rasio Keluar", if (totalPemasukan > 0) "${((totalPengeluaran.toFloat() / totalPemasukan) * 100).toInt()}%" else "0%", Icons.AutoMirrored.Filled.TrendingDown, ExpenseRed, isDarkMode, cardBg, textColor, Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Alokasi Kategori", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (categoryTotals.isEmpty()) {
                    item { Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { Text("Belum ada data pengeluaran", color = TextGray) } }
                } else {
                    items(categoryTotals.toList()) { (category, amount) ->
                        val percentage = if (totalPengeluaran > 0) amount.toFloat() / totalPengeluaran else 0f
                        CategoryStatItem(category, percentage, getCategoryColor(category), textColor, isDarkMode)
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when(category) {
        "Makanan" -> Color(0xFFE57373)
        "Transportasi" -> Color(0xFF81C784)
        "Hiburan" -> Color(0xFF64B5F6)
        "Belanja" -> Color(0xFFFFB74D)
        "Kesehatan" -> Color(0xFFBA68C8)
        "Tagihan" -> Color(0xFF4DB6AC)
        else -> Color(0xFF90A4AE)
    }
}

@Composable
fun FinancialOverviewCard(income: Long, expense: Long, isDarkMode: Boolean, cardBg: Color, textColor: Color) {
    val total = (income + expense).coerceAtLeast(1L)
    val expenseRatio = expense.toFloat() / total
    val animatedProgress by animateFloatAsState(targetValue = expenseRatio, animationSpec = tween(1000), label = "")

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = cardBg), elevation = CardDefaults.cardElevation(if(isDarkMode) 0.dp else 4.dp)) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    drawArc(color = if(isDarkMode) IncomeGreen.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.3f), startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round))
                    drawArc(brush = Brush.sweepGradient(listOf(ExpenseRed, Color(0xFFFF8A80), ExpenseRed)), startAngle = -90f, sweepAngle = animatedProgress * 360f, useCenter = false, style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pengeluaran", color = TextGray, fontSize = 14.sp)
                    Text("${(expenseRatio * 100).toInt()}%", color = textColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FinancialMiniStat("Pemasukan", income, IncomeGreen, textColor)
                VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = textColor.copy(alpha = 0.1f))
                FinancialMiniStat("Pengeluaran", expense, ExpenseRed, textColor)
            }
        }
    }
}

@Composable
fun FinancialMiniStat(label: String, amount: Long, color: Color, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(2.dp))); Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextGray, fontSize = 12.sp)
        }
        Text(formatRupiah(amount), color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PerformanceItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, isDarkMode: Boolean, cardBg: Color, textColor: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardBg), elevation = CardDefaults.cardElevation(if(isDarkMode) 0.dp else 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = "Performance Icon", tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, color = TextGray, fontSize = 12.sp)
            Text(value, color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CategoryStatItem(category: String, percentage: Float, color: Color, textColor: Color, isDarkMode: Boolean) {
    val animatedWidth by animateFloatAsState(targetValue = percentage, animationSpec = tween(1000), label = "")
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(category, color = textColor.copy(alpha = 0.9f), fontSize = 14.sp)
            Text("${(percentage * 100).toInt()}%", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(10.dp).background(if(isDarkMode) White.copy(alpha = 0.05f) else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))) {
            Box(modifier = Modifier.fillMaxWidth(animatedWidth.coerceIn(0f, 1f)).fillMaxHeight().background(color, RoundedCornerShape(5.dp)))
        }
    }
}
