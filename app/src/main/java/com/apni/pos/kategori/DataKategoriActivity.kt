package com.apni.pos.kategori

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.adapter.DetailKategoriAdapter
import com.apni.pos.viewmodel.DataKategoriViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DataKategoriActivity : AppCompatActivity() {

    private val viewModel: DataKategoriViewModel by viewModels()
    private lateinit var rvKategori: RecyclerView
    private lateinit var fabTambah: FloatingActionButton

    private lateinit var adapter: DetailKategoriAdapter
    private var listKategori: MutableList<ModelKategori> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kategori)

        init()
        fun showKategoriDetailFragment(kategori: ModelKategori) {
            Toast.makeText(this, "Klik: ${kategori.namaKategori}", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvKategori.layoutManager = layoutManager
        rvKategori.setHasFixedSize(true)

        viewModel.kategoriList.observe(this) { list ->
            adapter = DetailKategoriAdapter(list)
            rvKategori.adapter = adapter

            adapter.setOnClickListener(object : DetailKategoriAdapter.OnItemClickListener {
                override fun onItemClick(kategori: ModelKategori) {
                    if (!kategori.idKategori.isNullOrBlank()) {
                        showKategoriDetailFragment(kategori)
                    } else {
                        Toast.makeText(
                            this@DataKategoriActivity, "Galat: {getString(R.string.data_kategori_tidak_valid)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvKategori = findViewById(R.id.rvKategori)
        fabTambah = findViewById(R.id.fabTambah)
    }

}