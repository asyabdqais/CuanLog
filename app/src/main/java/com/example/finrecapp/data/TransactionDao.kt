package com.example.finrecapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY tanggal DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(nominal) FROM transactions WHERE jenis = 'Pemasukan'")
    fun getTotalPemasukan(): Flow<Long?>

    @Query("SELECT SUM(nominal) FROM transactions WHERE jenis = 'Pengeluaran'")
    fun getTotalPengeluaran(): Flow<Long?>

    @Query("SELECT SUM(nominal) FROM transactions WHERE jenis = 'Pemasukan' AND tanggal >= :startTime AND tanggal <= :endTime")
    suspend fun getIncomeInRange(startTime: Long, endTime: Long): Long?

    @Query("SELECT SUM(nominal) FROM transactions WHERE jenis = 'Pengeluaran' AND tanggal >= :startTime AND tanggal <= :endTime")
    suspend fun getExpenseInRange(startTime: Long, endTime: Long): Long?
}
