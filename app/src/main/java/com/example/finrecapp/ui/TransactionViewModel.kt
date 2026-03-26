package com.example.finrecapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finrecapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val transactionDao = database.transactionDao()
    private val userDao = database.userDao()
    private val notificationDao = database.notificationDao()

    // Add Dark Mode State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    
    val totalPemasukan: Flow<Long> = transactionDao.getTotalPemasukan().map { it ?: 0L }
    val totalPengeluaran: Flow<Long> = transactionDao.getTotalPengeluaran().map { it ?: 0L }

    val totalSaldo: StateFlow<Long> = combine(totalPemasukan, totalPengeluaran) { pemasukan, pengeluaran ->
        pemasukan - pengeluaran
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val monthlyGrowth: StateFlow<String> = allTransactions.map { transactions ->
        val calendar = Calendar.getInstance()
        val now = System.currentTimeMillis()
        calendar.timeInMillis = now
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startThisMonth = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startLastMonth = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endLastMonth = calendar.timeInMillis
        val thisMonthIncome = transactions.filter { it.tanggal >= startThisMonth && it.jenis == "Pemasukan" }.sumOf { it.nominal }
        val thisMonthExpense = transactions.filter { it.tanggal >= startThisMonth && it.jenis == "Pengeluaran" }.sumOf { it.nominal }
        val thisMonthNet = thisMonthIncome - thisMonthExpense
        val lastMonthIncome = transactions.filter { it.tanggal in startLastMonth..endLastMonth && it.jenis == "Pemasukan" }.sumOf { it.nominal }
        val lastMonthExpense = transactions.filter { it.tanggal in startLastMonth..endLastMonth && it.jenis == "Pengeluaran" }.sumOf { it.nominal }
        val lastMonthNet = lastMonthIncome - lastMonthExpense
        if (lastMonthNet == 0L && lastMonthIncome == 0L && lastMonthExpense == 0L) {
            if (thisMonthNet >= 0) "Awal yang baik bulan ini" else "Pengeluaran melebihi pemasukan"
        } else {
            val diff = thisMonthNet - lastMonthNet
            val divisor = if (lastMonthNet == 0L) 1.0 else Math.abs(lastMonthNet).toDouble()
            val percentage = (diff.toDouble() / divisor) * 100
            val prefix = if (diff >= 0) "Naik" else "Turun"
            "$prefix ${String.format("%.1f", Math.abs(percentage))}% dari bulan lalu"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Menghitung...")

    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val unreadNotifCount: Flow<Int> = notificationDao.getUnreadCount()

    fun addTransaction(nominal: Long, keterangan: String, jenis: String, tanggal: Long) {
        viewModelScope.launch {
            transactionDao.insertTransaction(TransactionEntity(nominal = nominal, keterangan = keterangan, jenis = jenis, tanggal = tanggal))
            notificationDao.insertNotification(NotificationEntity(title = if (jenis == "Pemasukan") "Pemasukan Baru" else "Pengeluaran Baru", message = "Transaksi '$keterangan' sebesar Rp $nominal telah dicatat.", timestamp = System.currentTimeMillis()))
        }
    }

    fun markNotificationsAsRead() { viewModelScope.launch { notificationDao.markAllAsRead() } }

    suspend fun registerUser(username: String, password: String, fullName: String, email: String): Boolean {
        if (userDao.registerUser(UserEntity(username, password, fullName, email)) != -1L) {
            notificationDao.insertNotification(NotificationEntity(title = "Selamat Datang!", message = "Halo $fullName, selamat bergabung.", timestamp = System.currentTimeMillis()))
            return true
        }
        return false
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return user != null && user.password == password
    }
}
