package com.apni.pos.outlet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModOutletBinding
import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.FirebaseDatabase

class ModOutletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModOutletBinding
    private var dataEdit: ModelOutlet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah ada data yang dikirim untuk diedit
        dataEdit = intent.getParcelableExtra("DATA_OUTLET")

        if (dataEdit != null) {
            binding.etNamaOutlet.setText(dataEdit?.namaOutlet)
            binding.etAlamatOutlet.setText(dataEdit?.alamatOutlet)
            binding.etNomorOutlet.setText(dataEdit?.nomorOutlet)
        }

        binding.btnSimpanOutlet.setOnClickListener { simpanKeFirebase() }
        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun simpanKeFirebase() {
        val nama = binding.etNamaOutlet.text.toString().trim()
        val alamat = binding.etAlamatOutlet.text.toString().trim()
        val nomor = binding.etNomorOutlet.text.toString().trim()

        if (nama.isEmpty() || alamat.isEmpty() || nomor.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("Outlet")

        // Gunakan ID lama jika edit, atau buat baru jika tambah
        val idOutlet = dataEdit?.idOutlet ?: dbRef.push().key!!

        val outletBaru = ModelOutlet(
            idOutlet = idOutlet,
            namaOutlet = nama,
            alamatOutlet = alamat,
            nomorOutlet = nomor
        )

        dbRef.child(idOutlet).setValue(outletBaru)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}