package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelKaryawan(
    var idKaryawan: String = "",
    var namaKaryawan: String = "",
    var nomorHp: String = "",
    var role: String = "Kasir"
) : Parcelable