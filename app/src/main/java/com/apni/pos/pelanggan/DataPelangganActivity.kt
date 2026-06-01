package com.apni.pos.pelanggan

import android.app.AlertDialog
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
import com.apni.pos.adapter.PelangganAdapter
import com.apni.pos.model.ModelPelanggan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class DataPelangganActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvPelanggan: RecyclerView
    private lateinit var adapter: PelangganAdapter
    private val listPelanggan = mutableListOf<ModelPelanggan>()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Pelanggan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pelanggan)

        etSearch = findViewById(R.id.etSearch)
        rvPelanggan = findViewById(R.id.rvData)

        findViewById<ImageView>(R.id.ivkembali).setOnClickListener { finish() }
        findViewById<FloatingActionButton>(R.id.fabTambah).setOnClickListener {
            startActivity(Intent(this, ModPelangganActivity::class.java))
        }

        setupRecyclerView()
        loadData()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = PelangganAdapter(
            listPelanggan,
            onPilih = { pelanggan ->
                // Kirim data pelanggan terpilih ke activity sebelumnya
                val result = Intent()
                result.putExtra("PELANGGAN_DIPILIH", pelanggan)
                setResult(RESULT_OK, result)
                finish()
            },
            onRiwayat = { pelanggan ->
                // TODO: buka RiwayatTransaksiActivity
                // val intent = Intent(this, RiwayatTransaksiActivity::class.java)
                // intent.putExtra("ID_PELANGGAN", pelanggan.idPelanggan)
                // startActivity(intent)
            },
            onEdit = { pelanggan ->
                val intent = Intent(this, ModPelangganActivity::class.java)
                intent.putExtra("ID_PELANGGAN", pelanggan.idPelanggan)
                startActivity(intent)
            },
            onHapus = { pelanggan -> konfirmasiHapus(pelanggan) }
        )
        rvPelanggan.layoutManager = LinearLayoutManager(this)
        rvPelanggan.adapter = adapter
    }

    private fun loadData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(ModelPelanggan::class.java)
                    item?.let { listPelanggan.add(it) }
                }
                adapter.updateData(listPelanggan.toList())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filter(text: String) {
        val filtered = listPelanggan.filter {
            it.namaPelanggan.contains(text, true) || it.nomorHp.contains(text)
        }
        adapter.updateData(filtered)
    }

    private fun konfirmasiHapus(pelanggan: ModelPelanggan) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Member")
            .setMessage("Yakin ingin menghapus ${pelanggan.namaPelanggan}?")
            .setPositiveButton("Hapus") { _, _ ->
                dbRef.child(pelanggan.idPelanggan).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Member berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}