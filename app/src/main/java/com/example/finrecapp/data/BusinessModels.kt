package com.example.finrecapp.data

import com.google.gson.annotations.SerializedName

// Model untuk Master Barang (Products)
data class Product(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("sku") val sku: String,
    @SerializedName("nama_barang") val namaBarang: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("stok") val stok: Int,
    @SerializedName("harga_beli_terakhir") val hargaBeliTerakhir: Long,
    @SerializedName("harga_jual") val hargaJual: Long
)

// Model untuk Supplier
data class Supplier(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nama_supplier") val namaSupplier: String,
    @SerializedName("kontak") val kontak: String,
    @SerializedName("alamat") val alamat: String
)

// Model untuk Pembelian (Purchases)
data class Purchase(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("no_faktur") val noFaktur: String,
    @SerializedName("tanggal") val tanggal: Long,
    @SerializedName("supplier_id") val supplierId: Int,
    @SerializedName("total_biaya") val totalBiaya: Long,
    @SerializedName("biaya_tambahan") val biayaTambahan: Long,
    @SerializedName("status_pembayaran") val statusPembayaran: String
)

// Model untuk Penjualan (Sales)
data class Sale(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("no_struk") val noStruk: String,
    @SerializedName("tanggal") val tanggal: Long,
    @SerializedName("metode_pembayaran") val metodePembayaran: String,
    @SerializedName("pelanggan_nama") val pelangganNama: String?,
    @SerializedName("kasir_nama") val kasirNama: String,
    @SerializedName("total_jual") val totalJual: Long,
    @SerializedName("total_laba") val totalLaba: Long
)

// Model untuk Detail Item (Purchases/Sales Detail)
data class TransactionItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("parent_id") val parentId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("qty") val qty: Int,
    @SerializedName("harga_satuan") val hargaSatuan: Long,
    @SerializedName("tipe") val tipe: String // 'Masuk' atau 'Keluar'
)
