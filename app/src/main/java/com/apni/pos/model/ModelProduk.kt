package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelProduk(
    val idProduk: String = "",
    val idKategori: String = "",
    val namaProduk: String = "",
    val hargaProduk: Double = 0.0,
    var qty: Int = 0
) : Parcelable