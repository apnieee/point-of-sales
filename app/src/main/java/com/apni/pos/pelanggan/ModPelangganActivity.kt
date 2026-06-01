package com.apni.pos.pelanggan

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.apni.pos.model.ModelPelanggan
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class ModPelangganActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etHp: TextInputEditText
    private lateinit var btnSimpan: MaterialButton
    private lateinit var tvJudul: TextView
    private var idPelanggan: String? = null
    private val dbRef = FirebaseDatabase.getInstance().getReference("Pelanggan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_pelanggan)

        etNama = findViewById(R.id.etNamaPelanggan)
        etHp = findViewById(R.id.etNomorHpPelanggan)
        btnSimpan = findViewById(R.id.btnSimpanPelanggan)
        tvJudul = findViewById(R.id.tvJudulForm)

        findViewById<ImageView>(R.id.ivkembali).setOnClickListener { finish() }

        idPelanggan = intent.getStringExtra("ID_PELANGGAN")
        if (idPelanggan != null) {
            tvJudul.text = "Edit Member"
            btnSimpan.text = "Simpan Perubahan"
            loadDetail()
        }

        btnSimpan.setOnClickListener { simpanData() }
    }

    private fun loadDetail() {
        dbRef.child(idPelanggan!!).get().addOnSuccessListener { snapshot ->
            val data = snapshot.getValue(ModelPelanggan::class.java)
            data?.let {
                etNama.setText(it.namaPelanggan)
                etHp.setText(it.nomorHp)
            }
        }
    }

    private fun simpanData() {
        val nama = etNama.text.toString().trim()
        val hp = etHp.text.toString().trim()

        if (nama.isEmpty()) { etNama.error = "Nama tidak boleh kosong"; return }
        if (hp.isEmpty()) { etHp.error = "Nomor HP tidak boleh kosong"; return }

        val id = idPelanggan ?: dbRef.push().key!!

        if (idPelanggan != null) {
            dbRef.child(id).child("namaPelanggan").setValue(nama)
            dbRef.child(id).child("nomorHp").setValue(hp)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            val data = ModelPelanggan(id, nama, hp, 0)
            dbRef.child(id).setValue(data).addOnCompleteListener {
                Toast.makeText(this, "Member baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}