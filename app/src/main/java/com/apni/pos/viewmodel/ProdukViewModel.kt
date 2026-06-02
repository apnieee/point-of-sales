package com.apni.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apni.pos.model.ModelProduk
import com.google.firebase.database.*

class ProdukViewModel : ViewModel() {

    private val _listProduk = MutableLiveData<List<ModelProduk>>()
    val listProduk: LiveData<List<ModelProduk>> get() = _listProduk

    private val dbRef = FirebaseDatabase.getInstance().getReference("Produk")

    init {
        loadProduk()
    }

    private fun loadProduk() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ModelProduk>()
                for (data in snapshot.children) {
                    val produk = data.getValue(ModelProduk::class.java)
                    produk?.let { list.add(it) }
                }
                _listProduk.value = list
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun hapusProduk(idProduk: String) {
        dbRef.child(idProduk).removeValue()
    }
}