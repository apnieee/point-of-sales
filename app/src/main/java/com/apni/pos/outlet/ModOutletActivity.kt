package com.apni.pos.outlet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModOutletBinding
import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.FirebaseDatabase

class ModOutletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModOutletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpanOutlet.setOnClickListener {
            simpanKeFirebase()
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun simpanKeFirebase() {
        val nama = binding.etNamaOutlet.text.toString().trim()
        val alamat = binding.etAlamatOutlet.text.toString().trim()
        val nomor = binding.etNomorOutlet.text.toString().trim()

        if (nama.isEmpty() || alamat.isEmpty() || nomor.isEmpty()) {
            Toast.makeText(this, "Semua kolom data wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Outlet")
        val idBaru = dbRef.push().key ?: return

        val outletBaru = ModelOutlet(
            idOutlet = idBaru,
            namaOutlet = nama,
            alamatOutlet = alamat,
            nomorOutlet = nomor,
            totalSistem = 0.0,
            totalAktual = 0.0
        )

        dbRef.child(idBaru).setValue(outletBaru)
            .addOnSuccessListener {
                Toast.makeText(this, "Cabang Outlet Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}