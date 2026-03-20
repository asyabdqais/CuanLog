package com.example.finrecapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nominal: Long,
    val keterangan: String,
    val jenis: String, // "Pemasukan" or "Pengeluaran"
    val kategori: String = "Lainnya",
    val tanggal: Long // Store as timestamp
)
