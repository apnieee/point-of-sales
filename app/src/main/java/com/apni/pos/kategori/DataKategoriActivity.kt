package com.apni.pos.kategori

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.adapter.DetailKategoriAdapter
import com.apni.pos.databinding.ActivityDataKategoriBinding
import com.apni.pos.model.ModelKategori
import com.apni.pos.viewmodel.DataKategoriViewModel

class DataKategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataKategoriBinding
    private val viewModel: DataKategoriViewModel by viewModels()
    private lateinit var adapterKategori: DetailKategoriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchBar()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, ModKategoriActivity::class.java))
        }

        binding.ivReload.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.listKategori.value?.let { adapterKategori.updateData(it) }
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapterKategori = DetailKategoriAdapter(emptyList()) { kategoriTerpilih ->
            val intent = Intent(this, ModKategoriActivity::class.java).apply {
                putExtra("DATA_KATEGORI", kategoriTerpilih)
            }
            startActivity(intent)
        }

        binding.rvKategori.apply {
            layoutManager = LinearLayoutManager(this@DataKategoriActivity)
            adapter = adapterKategori
        }

        viewModel.listKategori.observe(this) { list ->
            adapterKategori.updateData(list)
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val masterList = viewModel.listKategori.value ?: mutableListOf()
                val filteredList = masterList.filter { it.namaKategori.lowercase().contains(query) }
                adapterKategori.updateData(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}