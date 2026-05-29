package com.apni.pos.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelKategori(
    val idKategori: String = "",
    var namaKategori: String = "",
    @get:PropertyName("status_kategori")
    @set:PropertyName("status_kategori")
    var statusKategori: String = ""
) : Parcelable