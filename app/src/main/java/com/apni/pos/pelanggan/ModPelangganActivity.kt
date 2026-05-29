package com.apni.pos.pelanggan

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.apni.pos.model.ModelPelanggan
import com.google.firebase.database.FirebaseDatabase

class ModPelangganActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etHp: EditText
    private lateinit var btnSimpan: Button
    private var idPelanggan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_pelanggan) // Pastikan layout ini ada (isi sesuaikan dg Karyawan)

        etNama = findViewById(R.id.etNamaPelanggan)
        etHp = findViewById(R.id.etNomorHpPelanggan)
        btnSimpan = findViewById(R.id.btnSimpanPelanggan)

        idPelanggan = intent.getStringExtra("ID_PELANGGAN")
        if (idPelanggan != null) loadDetail()

        btnSimpan.setOnClickListener { simpanData() }
    }

    private fun simpanData() {
        val nama = etNama.text.toString()
        val hp = etHp.text.toString()
        val dbRef = FirebaseDatabase.getInstance().getReference("Pelanggan")

        val id = idPelanggan ?: dbRef.push().key!!
        val data = ModelPelanggan(id, nama, hp)

        dbRef.child(id).setValue(data).addOnCompleteListener {
            Toast.makeText(this, "Berhasil!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadDetail() {
        // Logika ambil data dari Firebase berdasarkan idPelanggan untuk ditampilkan di edittext
    }
}