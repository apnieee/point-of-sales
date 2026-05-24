package com.apni.pos.transaksi

data class ModelTransaksi(
    val id: String = "",
    val kode: String = "",
    val total: String = "0",
    val jam: String = "",
    val tanggal: String = "",
    val status: String = "",
    val pembayaran: String = "",
    val detail: String = ""
)