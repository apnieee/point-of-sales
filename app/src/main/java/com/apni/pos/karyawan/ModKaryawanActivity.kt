package com.apni.pos.karyawan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.apni.pos.model.ModelKaryawan
import com.google.firebase.database.FirebaseDatabase

class ModKaryawanActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etHp: EditText
    private lateinit var btnSimpan: Button
    private lateinit var ivKembali: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_karyawan)

        initComponent()

        btnSimpan.setOnClickListener {
            simpanKaryawanKeFirebase()
        }

        ivKembali.setOnClickListener { finish() }
    }

    private fun initComponent() {
        etNama = findViewById(R.id.etNamaKaryawan)
        etHp = findViewById(R.id.etNoHp)
        btnSimpan = findViewById(R.id.btnSimpanKaryawan)
        ivKembali = findViewById(R.id.ivkembali)
    }

    private fun simpanKaryawanKeFirebase() {
        val nama = etNama.text.toString().trim()
        val hp = etHp.text.toString().trim()

        if (nama.isEmpty() || hp.isEmpty()) {
            Toast.makeText(this, "Nama dan nomor HP tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Karyawan")
        val idBaru = dbRef.push().key ?: return

        val karyawanBaru = ModelKaryawan(
            idKaryawan = idBaru,
            namaKaryawan = nama,
            nomorHp = hp,
            role = "Kasir"
        )

        dbRef.child(idBaru).setValue(karyawanBaru)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Karyawan Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}