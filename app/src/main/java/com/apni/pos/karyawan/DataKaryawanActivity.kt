package com.apni.pos.karyawan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.adapter.KaryawanAdapter
import com.apni.pos.model.ModelKaryawan
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*

class DataKaryawanActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var ivReload: ImageView
    private lateinit var ivKembali: ImageView
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvPegawai: RecyclerView
    private lateinit var fabTambah: FloatingActionButton

    private lateinit var adapter: KaryawanAdapter
    private val listKaryawanFull = mutableListOf<ModelKaryawan>()
    private val listKaryawanFilter = mutableListOf<ModelKaryawan>()
    private val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Karyawan")

    private var filterRoleSaatIni: String = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_karyawan)

        initComponent()
        setupRecyclerView()
        ambilDataFirebase()

        fabTambah.setOnClickListener {
            startActivity(Intent(this, ModKaryawanActivity::class.java))
        }

        ivReload.setOnClickListener { ambilDataFirebase() }

        ivKembali.setOnClickListener { finish() }


        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                jalankanFilterData(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initComponent() {
        etSearch = findViewById(R.id.etSearchPegawai)
        ivReload = findViewById(R.id.ivReload)
        ivKembali = findViewById(R.id.ivKembali)
        chipGroup = findViewById(R.id.chipGroup)
        rvPegawai = findViewById(R.id.rvPegawai)
        fabTambah = findViewById(R.id.fabTambah)
    }

    private fun setupRecyclerView() {
        adapter = KaryawanAdapter(listKaryawanFilter) { karyawan, action ->
            if (action == "Edit") {
                val intent = Intent(this, ModKaryawanActivity::class.java)
                intent.putExtra("DATA", karyawan)
                startActivity(intent)
            } else if (action == "Hapus") {
                dbRef.child(karyawan.idKaryawan).removeValue()
            }
        }
        rvPegawai.layoutManager = LinearLayoutManager(this)
        rvPegawai.adapter = adapter
    }

    private fun ambilDataFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKaryawanFull.clear()
                for (data in snapshot.children) {
                    val karyawan = data.getValue(ModelKaryawan::class.java)
                    karyawan?.let { listKaryawanFull.add(it) }
                }
                jalankanFilterData(etSearch.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataKaryawanActivity, "Gagal memuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun jalankanFilterData(teksCari: String) {
        listKaryawanFilter.clear()
        val query = teksCari.lowercase(Locale.getDefault())

        for (item in listKaryawanFull) {
            val cocokNama = item.namaKaryawan.lowercase(Locale.getDefault()).contains(query)
            val cocokRole = filterRoleSaatIni == "Semua" || item.role.equals(filterRoleSaatIni, ignoreCase = true)

            if (cocokNama && cocokRole) {
                listKaryawanFilter.add(item)
            }
        }
        adapter.updateData(listKaryawanFilter)
    }
}