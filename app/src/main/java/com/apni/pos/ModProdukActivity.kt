package com.apni.pos.produk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModProdukBinding
import com.apni.pos.model.ModelKategori
import com.google.firebase.database.*

class ModProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModProdukBinding
    private val database = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
    private val produkRef = database.getReference("Produk")
    private val kategoriRef = database.getReference("Kategori")

    // Simpan pemetaan Nama Kategori -> ID Kategori
    private val mapKategori = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdownTipeUntung()
        setupDropdownKategoriRealtime()
        setupLogikaKalkulasiHarga()
        setupLogikaCheckboxStok()

        binding.ivkembali.setOnClickListener { finish() }

        binding.btnSimpanProduk.setOnClickListener {
            simpanProdukKeFirebase()
        }
    }

    private fun setupDropdownTipeUntung() {
        val listTipe = arrayOf("Persentase (%)", "Nominal (Rp)")
        val adapterTipe = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listTipe)
        binding.layoutHarga.actTipeKeuntungan.setAdapter(adapterTipe)
    }

    private fun setupDropdownKategoriRealtime() {
        kategoriRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaKategori = mutableListOf<String>()
                mapKategori.clear()
                for (data in snapshot.children) {
                    val kat = data.getValue(ModelKategori::class.java)
                    if (kat?.statusKategori == "Aktif") {
                        kat.namaKategori.let {
                            listNamaKategori.add(it)
                            mapKategori[it] = kat.idKategori ?: ""
                        }
                    }
                }
                val adapterKat = ArrayAdapter(this@ModProdukActivity, android.R.layout.simple_dropdown_item_1line, listNamaKategori)
                binding.layoutKategori.actPilihKategori.setAdapter(adapterKat)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupLogikaKalkulasiHarga() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungHargaJual()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.layoutHarga.etHargaBeli.addTextChangedListener(watcher)
        binding.layoutHarga.etNilaiProfit.addTextChangedListener(watcher)
        binding.layoutHarga.actTipeKeuntungan.setOnItemClickListener { _, _, _, _ -> hitungHargaJual() }
    }

    private fun hitungHargaJual() {
        val hargaBeliTxt = binding.layoutHarga.etHargaBeli.text.toString()
        val profitTxt = binding.layoutHarga.etNilaiProfit.text.toString()
        val tipeUntung = binding.layoutHarga.actTipeKeuntungan.text.toString()

        if (hargaBeliTxt.isEmpty() || profitTxt.isEmpty()) {
            binding.layoutHarga.etHargaJual.setText("")
            return
        }

        val hargaBeli = hargaBeliTxt.toDoubleOrNull() ?: 0.0
        val profit = profitTxt.toDoubleOrNull() ?: 0.0
        var hargaJual = 0.0

        if (tipeUntung.contains("%")) {
            hargaJual = hargaBeli + (hargaBeli * (profit / 100.0))
        } else {
            hargaJual = hargaBeli + profit
        }
        binding.layoutHarga.etHargaJual.setText(String.format("%.0f", hargaJual))
    }

    private fun setupLogikaCheckboxStok() {
        binding.layoutStok.cbStok.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutStok.etStok.setText("99999")
                binding.layoutStok.etStok.isEnabled = false
            } else {
                binding.layoutStok.etStok.setText("")
                binding.layoutStok.etStok.isEnabled = true
            }
        }
    }

    private fun simpanProdukKeFirebase() {
        val namaProduk = binding.layoutInformasi.etNamaProduk.text.toString().trim()
        val kategoriSelected = binding.layoutKategori.actPilihKategori.text.toString()
        val hargaJualTxt = binding.layoutHarga.etHargaJual.text.toString()
        val idKategori = mapKategori[kategoriSelected] ?: ""

        if (namaProduk.isEmpty() || kategoriSelected.isEmpty() || hargaJualTxt.isEmpty()) {
            Toast.makeText(this, "Lengkapi data produk!", Toast.LENGTH_SHORT).show()
            return
        }

        val idBaru = produkRef.push().key ?: return
        val dataProduk = HashMap<String, Any>()
        dataProduk["idProduk"] = idBaru
        dataProduk["idKategori"] = idKategori
        dataProduk["namaProduk"] = namaProduk
        dataProduk["namaKategori"] = kategoriSelected
        dataProduk["hargaProduk"] = hargaJualTxt.toDouble()
        dataProduk["qty"] = 0

        produkRef.child(idBaru).setValue(dataProduk)
            .addOnSuccessListener {
                Toast.makeText(this, "Produk Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}