package com.apni.pos.produk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModProdukBinding
import com.apni.pos.model.ModelKategori
import com.apni.pos.model.ModelOutlet
import com.apni.pos.model.ModelProduk
import com.google.firebase.database.*

class ModProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModProdukBinding
    private val database = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
    private val produkRef = database.getReference("Produk")
    private val kategoriRef = database.getReference("Kategori")
    private val outletRef = database.getReference("Outlet")

    private val mapKategori = mutableMapOf<String, String>()
    private val mapOutlet = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataEdit = intent.getParcelableExtra<ModelProduk>("DATA_PRODUK")
        if (dataEdit != null) {
            isiForm(dataEdit)
        }

        setupDropdownTipeUntung()
        setupDropdownKategoriRealtime()
        setupDropdownOutletRealtime()
        setupLogikaKalkulasiHarga()
        setupLogikaCheckboxStok()

        binding.ivkembali.setOnClickListener { finish() }
        binding.btnSimpanProduk.setOnClickListener { simpanProdukKeFirebase() }
    }

    private fun isiForm(produk: ModelProduk) {
        binding.layoutInformasi.etNamaProduk.setText(produk.namaProduk)
        binding.layoutHarga.etHargaBeli.setText(produk.hargaProduk.toString())
        binding.layoutStok.etStok.setText(produk.qty.toString())

        kategoriRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val kat = data.getValue(ModelKategori::class.java)
                    if (kat?.idKategori == produk.idKategori) {
                        binding.layoutKategoricabang.actPilihKategori.setText(kat.namaKategori, false)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.layoutKategoricabang.actPilihCabang.setText(produk.cabang, false)
    }

    private fun setupDropdownTipeUntung() {
        val listTipe = arrayOf("Persentase (%)", "Nominal (Rp)")
        val adapterTipe = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listTipe)
        // Sesuaikan dengan ID di layout harga Anda
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
                binding.layoutKategoricabang.actPilihKategori.setAdapter(adapterKat)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupDropdownOutletRealtime() {
        outletRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaOutlet = mutableListOf<String>()
                mapOutlet.clear()
                for (data in snapshot.children) {
                    val outlet = data.getValue(ModelOutlet::class.java)
                    if (outlet != null) {
                        listNamaOutlet.add(outlet.namaOutlet)
                        mapOutlet[outlet.namaOutlet] = outlet.idOutlet
                    }
                }
                val adapter = ArrayAdapter(this@ModProdukActivity, android.R.layout.simple_dropdown_item_1line, listNamaOutlet)
                binding.layoutKategoricabang.actPilihCabang.setAdapter(adapter)
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
        val hargaJual: Double = if (tipeUntung.contains("%")) {
            hargaBeli + (hargaBeli * profit / 100)
        } else {
            hargaBeli + profit
        }
        binding.layoutHarga.etHargaJual.setText(hargaJual.toLong().toString())
    }

    private fun simpanProdukKeFirebase() {
        val namaProduk = binding.layoutInformasi.etNamaProduk.text.toString().trim()
        val namaKategori = binding.layoutKategoricabang.actPilihKategori.text.toString()
        val namaOutlet = binding.layoutKategoricabang.actPilihCabang.text.toString()
        val hargaJual = binding.layoutHarga.etHargaJual.text.toString().toDoubleOrNull() ?: 0.0
        val stok = binding.layoutStok.etStok.text.toString().toIntOrNull() ?: 0

        val idKategori = mapKategori[namaKategori] ?: ""
        val idOutlet = mapOutlet[namaOutlet] ?: ""

        if (namaProduk.isEmpty() || idKategori.isEmpty() || idOutlet.isEmpty()) {
            Toast.makeText(this, "Lengkapi data produk dan pilih outlet!", Toast.LENGTH_SHORT).show()
            return
        }

        val dataEdit = intent.getParcelableExtra<ModelProduk>("DATA_PRODUK")
        val idSimpan = dataEdit?.idProduk ?: produkRef.push().key!!

        val dataProduk = ModelProduk(
            idProduk = idSimpan,
            idKategori = idKategori,
            namaProduk = namaProduk,
            hargaProduk = hargaJual,
            qty = stok,
            statusProduk = dataEdit?.statusProduk ?: "Aktif",
            cabang = namaOutlet,
            idOutlet = idOutlet
        )

        produkRef.child(idSimpan).setValue(dataProduk)
            .addOnSuccessListener {
                Toast.makeText(this, "Produk Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
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
}