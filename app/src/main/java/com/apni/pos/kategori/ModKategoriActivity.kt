package com.apni.pos.kategori

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModKategoriBinding
import com.apni.pos.model.ModelKategori
import com.google.firebase.database.FirebaseDatabase

class ModKategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModKategoriBinding
    private var dataLama: ModelKategori? = null
    private var isEditMode = false
    private val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Kategori")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdownStatus()
        cekIntentData()

        binding.btnSimpan.setOnClickListener { simpanKeFirebase() }
        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupDropdownStatus() {
        val listStatus = arrayOf("Aktif", "Tidak Aktif")
        val adapterDropdown = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listStatus)
        binding.actStatusKategori.setAdapter(adapterDropdown)
    }

    private fun cekIntentData() {
        dataLama = intent.getParcelableExtra("DATA_KATEGORI")
        if (dataLama != null) {
            isEditMode = true
            binding.etNamaKategori.setText(dataLama?.namaKategori)
            binding.actStatusKategori.setText(dataLama?.statusKategori, false)
            binding.btnSimpan.text = "Perbarui Kategori"
        }
    }

    private fun simpanKeFirebase() {
        val namaInput = binding.etNamaKategori.text.toString().trim()
        val statusInput = binding.actStatusKategori.text.toString()

        if (namaInput.isEmpty()) {
            binding.tilNamaKategori.error = "Nama kategori tidak boleh kosong!"
            return
        } else {
            binding.tilNamaKategori.error = null
        }

        val idKategori = if (isEditMode) {
            dataLama!!.idKategori
        } else {
            dbRef.push().key ?: "KAT${System.currentTimeMillis()}"
        }

        val kategoriData = ModelKategori(idKategori, namaInput, statusInput)

        dbRef.child(idKategori).setValue(kategoriData)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil simpan ke Firebase!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

        val idKategori = if (isEditMode) dataLama!!.idKategori else "KAT${System.currentTimeMillis().toString().takeLast(4)}"
}