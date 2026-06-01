package com.apni.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apni.pos.model.ModelProduk
import com.google.firebase.database.*

class ProdukViewModel : ViewModel() {

    // MutableLiveData: Kita bisa mengubah isinya di sini
    private val _listProduk = MutableLiveData<List<ModelProduk>>()
    // LiveData: Activity hanya bisa "melihat/mengamati" (tidak bisa mengubah)
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
                    // Mengambil data dari Firebase dan memasukkannya ke Model
                    val produk = data.getValue(ModelProduk::class.java)
                    produk?.let { list.add(it) }
                }
                _listProduk.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error jika koneksi gagal
            }
        })
    }

    // Fungsi untuk menambah, update, atau hapus produk nanti
    fun hapusProduk(idProduk: String) {
        dbRef.child(idProduk).removeValue()
    }
}