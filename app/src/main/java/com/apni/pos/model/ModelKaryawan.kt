package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelKaryawan(
    var idKaryawan: String = "",
    var namaKaryawan: String = "",
    var username: String = "",
    var pin: String = "",
    var nomorHp: String = "",
    var role: String = "Kasir",
    var outlet: String = "Pusat",
    var isAktif: Boolean = true
) : Parcelable