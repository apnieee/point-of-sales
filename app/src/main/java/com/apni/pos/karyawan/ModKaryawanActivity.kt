package com.apni.pos.karyawan

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.apni.pos.databinding.ActivityModKaryawanBinding
import com.apni.pos.model.ModelKaryawan
import com.google.firebase.database.FirebaseDatabase

class ModKaryawanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModKaryawanBinding
    private val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Karyawan")

    private var dataEdit: ModelKaryawan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModKaryawanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()

        dataEdit = intent.getParcelableExtra("DATA_KARYAWAN")
        if (dataEdit != null) {
            isiForm(dataEdit!!)
        }

        binding.btnSimpanKaryawan.setOnClickListener {
            simpanKaryawanKeFirebase()
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupSpinners() {
        val roles = arrayOf("Admin", "Kasir", "Owner")
        val adapterRole = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.autoRole.setAdapter(adapterRole)

        val outlets = arrayOf("Outlet Pusat", "Cabang Solo", "Cabang Seoul")
        val adapterOutlet = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, outlets)
        binding.autoOutlet.setAdapter(adapterOutlet)
    }

    private fun isiForm(karyawan: ModelKaryawan) {
        binding.etNamaKaryawan.setText(karyawan.namaKaryawan)
        binding.etUsername.setText(karyawan.username)
        binding.etPin.setText(karyawan.pin)
        binding.etNoHp.setText(karyawan.nomorHp)
        binding.autoRole.setText(karyawan.role, false)
        binding.autoOutlet.setText(karyawan.outlet, false)
        binding.switchStatus.isChecked = karyawan.isAktif
    }

    private fun simpanKaryawanKeFirebase() {
        val nama = binding.etNamaKaryawan.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val pin = binding.etPin.text.toString().trim()
        val noHp = binding.etNoHp.text.toString().trim()
        val role = binding.autoRole.text.toString()
        val outlet = binding.autoOutlet.text.toString()
        val status = binding.switchStatus.isChecked

        if (nama.isEmpty() || username.isEmpty() || pin.isEmpty() || noHp.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val idKaryawan = dataEdit?.idKaryawan ?: dbRef.push().key!!

        val karyawanBaru = ModelKaryawan(
            idKaryawan = idKaryawan,
            namaKaryawan = nama,
            username = username,
            pin = pin,
            nomorHp = noHp,
            role = role,
            outlet = outlet,
            isAktif = status
        )

        dbRef.child(idKaryawan).setValue(karyawanBaru)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}