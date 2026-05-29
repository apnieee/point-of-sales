package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelTransaksi(
    val kodeTransaksi: String = "",
    val tanggal: String = "",
    val jam: String = "",
    val metodePembayaran: String = "",
    val subtotal: Double = 0.0,
    val diskon: Double = 0.0,
    val pajak: Double = 0.0,
    val totalBayar: Double = 0.0,
    val jumlahUangBayar: Double = 0.0,
    val kembalian: Double = 0.0,
    val detailPesananTeks: String = "" // Rangkuman item-item yang dibeli dalam bentuk teks
) : Parcelable