package com.apni.pos.transaksi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.adapter.AdapterKeranjang
import com.apni.pos.adapter.AdapterProdukTransaksi
import com.apni.pos.databinding.ActivityTransaksiBinding
import com.apni.pos.databinding.BottomsheetCheckoutBinding
import com.apni.pos.databinding.BottomsheetKeranjangBinding
import com.apni.pos.model.ModelKategori
import com.apni.pos.model.ModelProduk
import com.apni.pos.model.ModelTransaksi
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var adapterProduk: AdapterProdukTransaksi
    private var listMasterSeblak = mutableListOf<ModelProduk>()

    private var produkListener: com.google.firebase.database.ValueEventListener? = null
    private var kategoriListener: com.google.firebase.database.ValueEventListener? = null

    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ambilDataProdukFirebase()
        setupRecyclerView()

        binding.btnCheckout.setOnClickListener {
            bukaBottomSheetKeranjang()
        }

        binding.ivKembali.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        produkListener?.let { FirebaseDatabase.getInstance().getReference("Produk").removeEventListener(it) }
        kategoriListener?.let { FirebaseDatabase.getInstance().getReference("Kategori").removeEventListener(it) }
    }

    private fun ambilDataProdukFirebase() {
        val dbProdukRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Produk")

        val dbKategoriRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Kategori")

        dbKategoriRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(kategoriSnapshot: com.google.firebase.database.DataSnapshot) {
                val listKategoriAktif = mutableListOf<String>()
                for (data in kategoriSnapshot.children) {
                    val kat = data.getValue(ModelKategori::class.java)
                    if (kat?.statusKategori == "Aktif") {
                        listKategoriAktif.add(kat.idKategori)
                    }
                }

                dbProdukRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(produkSnapshot: com.google.firebase.database.DataSnapshot) {
                        listMasterSeblak.clear()
                        for (data in produkSnapshot.children) {
                            val produk = data.getValue(ModelProduk::class.java)
                            produk?.let {
                                if (listKategoriAktif.contains(it.idKategori)) {
                                    it.qty = 0
                                    listMasterSeblak.add(it)
                                }
                            }
                        }
                        adapterProduk.updateData(listMasterSeblak)
                        hitungTotalHalamanUtama()
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
                })
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    private fun setupRecyclerView() {
        adapterProduk = AdapterProdukTransaksi(listMasterSeblak) { hitungTotalHalamanUtama() }
        binding.rvProduk.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduk.adapter = adapterProduk
        hitungTotalHalamanUtama()
    }

    private fun hitungTotalHalamanUtama() {
        var totalItem = 0
        var totalHarga = 0.0
        for (p in listMasterSeblak) {
            if (p.qty > 0) {
                totalItem += p.qty
                totalHarga += (p.hargaProduk * p.qty)
            }
        }
        binding.tvTotalItem.text = totalItem.toString()
        binding.tvTotalHarga.text = formatRupiah.format(totalHarga)
        binding.btnCheckout.isEnabled = totalItem > 0
    }

    private fun bukaBottomSheetKeranjang() {
        val dialog = BottomSheetDialog(this)
        val bsBinding = BottomsheetKeranjangBinding.inflate(layoutInflater)
        dialog.setContentView(bsBinding.root)

        var produkAktif = listMasterSeblak.filter { it.qty > 0 }.toMutableList()
        bsBinding.tvJumlahItem.text = "${produkAktif.sumOf { it.qty }} item"

        val adapterKeranjang = AdapterKeranjang(produkAktif) {
            produkAktif.clear()
            produkAktif.addAll(listMasterSeblak.filter { it.qty > 0 })

            hitungTotalHalamanUtama()

            val total = produkAktif.sumOf { it.hargaProduk * it.qty }
            bsBinding.tvSubtotal.text = formatRupiah.format(total)
            bsBinding.tvJumlahItem.text = "${produkAktif.sumOf { it.qty }} item"

            if (produkAktif.isEmpty()) {
                dialog.dismiss()
            }
        }

        bsBinding.rvKeranjang.layoutManager = LinearLayoutManager(this)
        bsBinding.rvKeranjang.adapter = adapterKeranjang
        bsBinding.tvSubtotal.text = formatRupiah.format(produkAktif.sumOf { it.hargaProduk * it.qty })

        bsBinding.btnLanjutCheckout.setOnClickListener {
            dialog.dismiss()
            bukaBottomSheetCheckout(produkAktif)
        }
        dialog.show()
    }

    private fun bukaBottomSheetCheckout(produkAktif: List<ModelProduk>) {
        val dialog = BottomSheetDialog(this)
        val bsBinding = BottomsheetCheckoutBinding.inflate(layoutInflater)
        dialog.setContentView(bsBinding.root)

        // Hitungan biaya
        val subtotal = produkAktif.sumOf { it.hargaProduk * it.qty }
        val pajak = subtotal * 0.10
        val totalFinal = subtotal + pajak

        // Set teks ke UI
        bsBinding.tvCheckoutTotal.text = formatRupiah.format(totalFinal)

        // RecyclerView untuk list pesanan di dalam BottomSheet (Penting!)
        // Pastikan AdapterKeranjang digunakan kembali agar user bisa melihat apa yang dibayar
        bsBinding.rvDetailPesanan.layoutManager = LinearLayoutManager(this)
        bsBinding.rvDetailPesanan.adapter = AdapterKeranjang(produkAktif.toMutableList()) {
            // Logika jika ada perubahan di dalam bottomsheet (opsional)
        }

        bsBinding.etJumlahBayar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Logic kembalian bisa ditambah di sini jika ada TextView tvKembalian
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        bsBinding.btnBayar.setOnClickListener {
            val uangInput = bsBinding.etJumlahBayar.text.toString().trim().toDoubleOrNull() ?: 0.0

            if (uangInput < totalFinal) {
                Toast.makeText(this, "Pembayaran kurang!", Toast.LENGTH_SHORT).show()
            } else {
                prosesSimpanTransaksi(produkAktif, subtotal, pajak, totalFinal, uangInput, dialog)
            }
        }
        dialog.show()
    }
    private fun prosesSimpanTransaksi(
        produkAktif: List<ModelProduk>,
        subtotal: Double,
        pajak: Double,
        totalFinal: Double,
        uangInput: Double,
        dialog: BottomSheetDialog
    ) {
        // Generate Kode Nota
        val sdfTanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdfJam = SimpleDateFormat("HH:mm", Locale.getDefault())
        val notaId = "TRX${System.currentTimeMillis().toString().takeLast(6)}"

        var teksPesanan = ""
        produkAktif.forEach { teksPesanan += "${it.namaProduk} x${it.qty}\n" }

        // Catatan: Jika ada chipGroup di XML checkout, pastikan cara mengambilnya tepat
        val metodeTerpilih = "Tunai" // Sesuaikan dengan logika pilihan metode Anda

        val transaksiSelesai = ModelTransaksi(
            kodeTransaksi = notaId,
            tanggal = sdfTanggal.format(Date()),
            jam = sdfJam.format(Date()),
            metodePembayaran = metodeTerpilih,
            subtotal = subtotal,
            pajak = pajak,
            totalBayar = totalFinal,
            jumlahUangBayar = uangInput,
            kembalian = uangInput - totalFinal,
            detailPesananTeks = teksPesanan
        )

        val dbTransaksiRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Transaksi")

        dbTransaksiRef.child(notaId).setValue(transaksiSelesai)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                val intent = Intent(this, DetailTransaksiActivity::class.java).apply {
                    putExtra("DATA_NOTA", transaksiSelesai)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firebase Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}