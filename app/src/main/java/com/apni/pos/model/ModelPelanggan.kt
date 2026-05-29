package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelPelanggan(
    var idPelanggan: String = "",
    var namaPelanggan: String = "",
    var nomorHp: String = "",
    var poin: Int = 0
) : Parcelable