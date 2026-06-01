package com.apni.pos.produk

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.adapter.DetailProdukAdapter
import com.apni.pos.databinding.ActivityDataProdukBinding
import com.apni.pos.model.ModelProduk
import com.apni.pos.viewmodel.KategoriViewModel
import com.apni.pos.viewmodel.ProdukViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataProdukActivity : AppCompatActivity() {
    private val produkViewModel: ProdukViewModel by viewModels()
    private val kategoriViewModel: KategoriViewModel by viewModels()

    private lateinit var binding: ActivityDataProdukBinding
    private val database = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
    private val produkRef = database.getReference("Produk")

    private val masterListProduk = mutableListOf<ModelProduk>()

    private lateinit var adapterProduk: DetailProdukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        ambilDataFirebaseRealtime()
        setupSearchBar()
        loadKategoriKeChip()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, ModProdukActivity::class.java))
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapterProduk = DetailProdukAdapter(emptyList()) { produkTerpilih ->
            val intent = Intent(this, ModProdukActivity::class.java).apply {
                putExtra("DATA_PRODUK", produkTerpilih)
            }
            startActivity(intent)
        }

        binding.rvProduk.apply {
            layoutManager = LinearLayoutManager(this@DataProdukActivity)
            adapter = adapterProduk
        }
    }

    private fun ambilDataFirebaseRealtime() {
        produkRef.keepSynced(true)
        produkRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                masterListProduk.clear()
                for (data in snapshot.children) {
                    val produk = data.getValue(ModelProduk::class.java)
                    produk?.let { masterListProduk.add(it) }
                }
                adapterProduk.updateData(masterListProduk)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataProdukActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()

                val filteredList = masterListProduk.filter {
                    it.namaProduk.lowercase().contains(query)
                }

                adapterProduk.updateData(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadKategoriKeChip() {
        val chipGroup = binding.chipGroup
        chipGroup.removeAllViews()

        val chipSemua = Chip(this)
        chipSemua.text = "Semua"
        chipSemua.isCheckable = true
        chipSemua.isChecked = true
        chipSemua.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) adapterProduk.updateData(masterListProduk)
        }
        chipGroup.addView(chipSemua)

        kategoriViewModel.listKategori.observe(this) { listKategori ->
            listKategori.forEach { kategori ->
                if (kategori.statusKategori.equals("Aktif", ignoreCase = true)) {
                    val chip = Chip(this)
                    chip.text = kategori.namaKategori
                    chip.isCheckable = true

                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            filterProdukByKategori(kategori.namaKategori)
                        }
                    }
                    chipGroup.addView(chip)
                }
            }
        }
    }

    private fun filterProdukByKategori(namaKategori: String) {
        if (namaKategori == "Semua") {
            adapterProduk.updateData(masterListProduk)
        } else {
            val filtered = masterListProduk.filter {
                it.idKategori.equals(namaKategori, ignoreCase = true)
            }
            adapterProduk.updateData(filtered)
        }
    }
}