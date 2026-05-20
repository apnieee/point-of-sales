package com.apni.pos.kategori

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase

class ModKategoriActivity : AppCompatActivity() {
    private lateinit var etNamaKategori: TextInputEditText
    private lateinit var tilNamaKategori: TextInputLayout
    private lateinit var tilStatusKategori: TextInputLayout
    private lateinit var actStatusKategori: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton
    private lateinit var ivKembali: ImageView

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("kategori")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_kategori)
        init()
        setupDropdown()

        btnSimpan.setOnClickListener { simpan() }
        ivKembali.setOnClickListener { finish() }
    }

    private fun setupDropdown() {
        val statusOptions = resources.getStringArray(R.array.status)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusOptions)
        actStatusKategori.setAdapter(adapter)
    }

    private fun simpan() {
        val nama = etNamaKategori.text.toString().trim()
        val selectedStatus = actStatusKategori.text.toString().trim()

        if (nama.isEmpty()) {
            tilNamaKategori.error = "Nama kategori tidak boleh kosong"
            return
        } else {
            tilNamaKategori.error = null
        }

        if (selectedStatus.isEmpty()) {
            tilStatusKategori.error = "Status tidak boleh kosong"
            return
        } else {
            tilStatusKategori.error = null
        }

        val kategoriBaru = myRef.push()
        val kategoriId = kategoriBaru.key

        val data = mapOf(
            "id" to kategoriId,
            "namaKategori" to nama,
            "statusKategori" to selectedStatus
        )

        kategoriBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Kategori berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun init() {
        etNamaKategori = findViewById(R.id.etNamaKategori)
        tilNamaKategori = findViewById(R.id.tilNamaKategori)
        tilStatusKategori = findViewById(R.id.tilStatusKategori)
        actStatusKategori = findViewById(R.id.actStatusKategori)
        btnSimpan = findViewById(R.id.btnSimpan)
        ivKembali = findViewById(R.id.ivkembali)
    }
}