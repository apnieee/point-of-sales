package com.apni.pos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelOutlet(
    var idOutlet: String = "",
    var namaOutlet: String = "",
    var alamatOutlet: String = "",
    var nomorOutlet: String = "",
    var totalSistem: Double = 0.0,
    var totalAktual: Double = 0.0
) : Parcelable