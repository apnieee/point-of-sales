package com.apni.pos.karyawan

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModKaryawanBinding
import com.apni.pos.model.ModelKaryawan
import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ModKaryawanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModKaryawanBinding

    private val dbKaryawan = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Karyawan")
    private val dbOutlet = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Outlet")

    private var dataEdit: ModelKaryawan? = null

    private val listOutletData = mutableListOf<ModelOutlet>()
    private var idOutletTerpilih: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModKaryawanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSpinner()

        ambilDataOutletFirebase()

        dataEdit = intent.getParcelableExtra("DATA_KARYAWAN")

        binding.btnSimpanKaryawan.setOnClickListener {
            simpanKaryawanKeFirebase()
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Admin", "Kasir", "Owner")
        val adapterRole = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.autoRole.setAdapter(adapterRole)
    }

    private fun ambilDataOutletFirebase() {
        dbOutlet.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaOutlet = mutableListOf<String>()
                listOutletData.clear()

                for (data in snapshot.children) {
                    val outlet = data.getValue(ModelOutlet::class.java)
                    outlet?.let {
                        listOutletData.add(it)
                        listNamaOutlet.add(it.namaOutlet)
                    }
                }

                val adapterOutlet = ArrayAdapter(this@ModKaryawanActivity, android.R.layout.simple_dropdown_item_1line, listNamaOutlet)
                binding.autoOutlet.setAdapter(adapterOutlet)

                dataEdit?.let { isiForm(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModKaryawanActivity, "Gagal ambil data outlet", Toast.LENGTH_SHORT).show()
            }
        })

        binding.autoOutlet.setOnItemClickListener { parent, _, position, _ ->
            val namaTerpilih = parent.getItemAtPosition(position).toString()
            idOutletTerpilih = listOutletData.find { it.namaOutlet == namaTerpilih }?.idOutlet ?: ""
        }
    }

    private fun isiForm(karyawan: ModelKaryawan) {
        binding.etNamaKaryawan.setText(karyawan.namaKaryawan)
        binding.etUsername.setText(karyawan.username)
        binding.etPin.setText(karyawan.pin)
        binding.etNoHp.setText(karyawan.nomorHp)
        binding.autoRole.setText(karyawan.role, false)
        binding.autoOutlet.setText(karyawan.outlet, false) // Nama outlet
        idOutletTerpilih = karyawan.idOutlet // Simpan ID lama
        binding.switchStatus.isChecked = karyawan.isAktif
    }

    private fun simpanKaryawanKeFirebase() {
        val nama = binding.etNamaKaryawan.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val pin = binding.etPin.text.toString().trim()
        val noHp = binding.etNoHp.text.toString().trim()
        val role = binding.autoRole.text.toString()
        val namaOutlet = binding.autoOutlet.text.toString()
        val status = binding.switchStatus.isChecked

        if (nama.isEmpty() || username.isEmpty() || pin.isEmpty() || noHp.isEmpty() || role.isEmpty() || namaOutlet.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data termasuk Outlet!", Toast.LENGTH_SHORT).show()
            return
        }

        val idKaryawan = dataEdit?.idKaryawan ?: dbKaryawan.push().key!!

        val karyawanBaru = ModelKaryawan(
            idKaryawan = idKaryawan,
            namaKaryawan = nama,
            username = username,
            pin = pin,
            nomorHp = noHp,
            role = role,
            outlet = namaOutlet,
            idOutlet = idOutletTerpilih,
            isAktif = status
        )

        dbKaryawan.child(idKaryawan).setValue(karyawanBaru)
            .addOnSuccessListener {
                Toast.makeText(this, "Data karyawan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}