package com.example.finrecapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finrecapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    // Base URL - Pastikan IP ini sama dengan IP Laptop Anda saat ini
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.178.60.76/finrec_api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    // --- State Mode Personal (Langsung dari MySQL via API) ---
    private val _allTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val allTransactions: StateFlow<List<TransactionEntity>> = _allTransactions.asStateFlow()

    private val _allNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val allNotifications: StateFlow<List<NotificationEntity>> = _allNotifications.asStateFlow()

    // --- State Mode Business ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    private val _businessReport = MutableStateFlow<BusinessReport?>(null)
    val businessReport: StateFlow<BusinessReport?> = _businessReport.asStateFlow()

    private val _purchaseHistory = MutableStateFlow<List<Purchase>>(emptyList())
    val purchaseHistory: StateFlow<List<Purchase>> = _purchaseHistory.asStateFlow()

    private val _saleHistory = MutableStateFlow<List<Sale>>(emptyList())
    val saleHistory: StateFlow<List<Sale>> = _saleHistory.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        fetchAllDataFromServer()
    }

    fun fetchAllDataFromServer() {
        fetchTransactions()
        fetchNotifications()
        fetchProducts()
        fetchSuppliers()
        fetchBusinessReport()
        fetchBusinessHistory()
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    // --- Kalkulasi Data Personal dari StateFlow ---
    val totalPemasukan: Flow<Long> = allTransactions.map { list ->
        list.filter { it.jenis == "Pemasukan" }.sumOf { it.nominal }
    }
    
    val totalPengeluaran: Flow<Long> = allTransactions.map { list ->
        list.filter { it.jenis == "Pengeluaran" }.sumOf { it.nominal }
    }

    val totalSaldo: StateFlow<Long> = allTransactions.map { list ->
        val income = list.filter { it.jenis == "Pemasukan" }.sumOf { it.nominal }
        val expense = list.filter { it.jenis == "Pengeluaran" }.sumOf { it.nominal }
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val monthlyGrowth: StateFlow<String> = allTransactions.map { transactions ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startThisMonth = calendar.timeInMillis
        val thisMonthNet = transactions.filter { it.tanggal >= startThisMonth }
            .sumOf { if (it.jenis == "Pemasukan") it.nominal else -it.nominal }
        "Bulan ini: Rp $thisMonthNet"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Menghitung...")

    val unreadNotifCount: Flow<Int> = allNotifications.map { list ->
        list.count { !it.isRead }
    }

    // --- Fungsi API Personal ---
    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val response = apiService.getTransactions()
                if (response.isSuccessful) {
                    _allTransactions.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Fetch Trans Gagal: ${e.message}") }
        }
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val response = apiService.getNotifications()
                if (response.isSuccessful) {
                    _allNotifications.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Fetch Notif Gagal: ${e.message}") }
        }
    }

    fun addTransaction(nominal: Long, keterangan: String, jenis: String, tanggal: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.addTransaction(nominal, keterangan, jenis, tanggal)
                if (response.isSuccessful) {
                    fetchTransactions() // Refresh daftar transaksi
                    fetchBusinessReport() // Update laporan jika ada pengaruh
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Add Trans Gagal: ${e.message}") }
        }
    }

    // --- Fungsi API Business ---
    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error fetch products: ${e.message}") }
        }
    }

    fun fetchSuppliers() {
        viewModelScope.launch {
            try {
                val response = apiService.getSuppliers()
                if (response.isSuccessful) {
                    _suppliers.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error fetch suppliers: ${e.message}") }
        }
    }

    fun fetchBusinessReport() {
        viewModelScope.launch {
            try {
                val response = apiService.getBusinessReport()
                if (response.isSuccessful) {
                    _businessReport.value = response.body()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error fetch report: ${e.message}") }
        }
    }

    fun fetchBusinessHistory() {
        viewModelScope.launch {
            try {
                val resP = apiService.getPurchaseHistory()
                if (resP.isSuccessful) _purchaseHistory.value = resP.body() ?: emptyList()
                
                val resS = apiService.getSaleHistory()
                if (resS.isSuccessful) _saleHistory.value = resS.body() ?: emptyList()
            } catch (e: Exception) { Log.e("API_DEBUG", "Error fetch history: ${e.message}") }
        }
    }

    fun addProduct(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.addProduct(product)
                if (response.isSuccessful) {
                    fetchProducts()
                    onSuccess()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error add product: ${e.message}") }
        }
    }

    fun addSupplier(supplier: Supplier, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.addSupplier(supplier)
                if (response.isSuccessful) {
                    fetchSuppliers()
                    onSuccess()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error add supplier: ${e.message}") }
        }
    }

    fun addPurchase(purchase: Purchase, items: List<TransactionItem>) {
        viewModelScope.launch {
            try {
                val response = apiService.addPurchase(PurchaseWithItems(purchase, items))
                if (response.isSuccessful) {
                    fetchTransactions() // Update saldo personal (Pengeluaran)
                    fetchProducts()
                    fetchBusinessReport()
                    fetchBusinessHistory()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error add purchase: ${e.message}") }
        }
    }

    fun addSale(sale: Sale, items: List<TransactionItem>) {
        viewModelScope.launch {
            try {
                val response = apiService.addSale(SaleWithItems(sale, items))
                if (response.isSuccessful) {
                    fetchTransactions() // Update saldo personal (Pemasukan)
                    fetchProducts()
                    fetchBusinessReport()
                    fetchBusinessHistory()
                }
            } catch (e: Exception) { Log.e("API_DEBUG", "Error add sale: ${e.message}") }
        }
    }

    fun markNotificationsAsRead() {
        // Implementasi mark read di server via API jika diperlukan
    }

    suspend fun registerUser(username: String, password: String, fullName: String, email: String): Boolean {
        return try {
            val response = apiService.registerUser(username, password, fullName, email)
            response.isSuccessful && response.body()?.status == "success"
        } catch (e: Exception) { false }
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        return try {
            val response = apiService.loginUser(username, password)
            response.isSuccessful && response.body()?.status == "success"
        } catch (e: Exception) { false }
    }
}
