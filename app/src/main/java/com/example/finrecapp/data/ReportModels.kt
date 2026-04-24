package com.example.finrecapp.data

import com.google.gson.annotations.SerializedName

data class BusinessReport(
    @SerializedName("omzet") val omzet: Long,
    @SerializedName("hpp") val hpp: Long,
    @SerializedName("laba") val laba: Long,
    @SerializedName("total_stok") val totalStok: Int
)
