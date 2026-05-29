package com.apni.pos.pelanggan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var ivKembali: ImageView
    private lateinit var rvPelanggan: RecyclerView
    private lateinit var fabTambah: FloatingActionButton
    private lateinit var adapter: PelangganAdapter

    private val listPelanggan = mutableListOf<ModelPelanggan>()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Pelanggan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pelanggan)

        initComponent()
        setupRecyclerView()
        loadData()

        ivKembali.setOnClickListener { finish() }
        fabTambah.setOnClickListener { startActivity(Intent(this, ModPelangganActivity::class.java)) }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun initComponent() {
        etSearch = findViewById(R.id.etSearch)
        ivKembali = findViewById(R.id.ivkembali)
        rvPelanggan = findViewById(R.id.rvPelanggan)
        fabTambah = findViewById(R.id.fabTambah)
    }

    private fun setupRecyclerView() {
        adapter = PelangganAdapter(listPelanggan) { pelanggan ->
            val intent = Intent(this, ModPelangganActivity::class.java)
            intent.putExtra("ID_PELANGGAN", pelanggan.idPelanggan)
            startActivity(intent)
        }
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
                adapter.updateData(listPelanggan)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filter(text: String) {
        val filteredList = listPelanggan.filter {
            it.namaPelanggan.contains(text, true) || it.nomorHp.contains(text)
        }
        adapter.updateData(filteredList)
    }
}