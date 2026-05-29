package com.apni.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apni.pos.model.ModelKategori
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataKategoriViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
        .getReference("Kategori")

    private val _listKategori = MutableLiveData<List<ModelKategori>>()
    val listKategori: LiveData<List<ModelKategori>> get() = _listKategori

    init {
        ambilDataDariFirebase()
    }

    private fun ambilDataDariFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ModelKategori>()
                for (data in snapshot.children) {
                    val kategori = data.getValue(ModelKategori::class.java)
                    kategori?.let { items.add(it) }
                }
                _listKategori.value = items
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getKategoriAktifOnly(): List<ModelKategori> {
        return _listKategori.value?.filter { it.statusKategori == "Aktif" } ?: emptyList()
    }
}