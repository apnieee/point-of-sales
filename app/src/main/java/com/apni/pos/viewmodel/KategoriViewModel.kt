package com.apni.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apni.pos.model.ModelKategori
import com.google.firebase.database.*

class KategoriViewModel : ViewModel() {

    private val _listKategori = MutableLiveData<List<ModelKategori>>()
    val listKategori: LiveData<List<ModelKategori>> get() = _listKategori

    private val dbRef = FirebaseDatabase.getInstance().getReference("Kategori")

    init {
        loadKategori()
    }

    private fun loadKategori() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ModelKategori>()
                for (data in snapshot.children) {
                    val kategori = data.getValue(ModelKategori::class.java)
                    kategori?.let { list.add(it) }
                }
                _listKategori.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}