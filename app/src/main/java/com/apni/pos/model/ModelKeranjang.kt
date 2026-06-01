package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelKeranjang(
    var idProduk: String = "",
    var namaProduk: String = "",
    var hargaProduk: Double = 0.0,
    var jumlahBeli: Int = 0,
    var totalHargaItem: Double = 0.0
) : Parcelable