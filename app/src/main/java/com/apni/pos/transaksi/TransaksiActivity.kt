package com.apni.pos.transaksi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.apni.pos.model.ModelKeranjang
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

        val subtotal = produkAktif.sumOf { it.hargaProduk * it.qty }
        val pajak = subtotal * 0.10
        val totalFinal = subtotal + pajak

        bsBinding.tvCheckoutSubtotal.text = formatRupiah.format(subtotal)
        bsBinding.tvCheckoutPajak.text = formatRupiah.format(pajak)
        bsBinding.tvCheckoutTotal.text = formatRupiah.format(totalFinal)

        bsBinding.rvDetailPesanan.layoutManager = LinearLayoutManager(this)
        bsBinding.rvDetailPesanan.adapter = AdapterKeranjang(produkAktif.toMutableList()) {}

        // Hitung kembalian realtime
        bsBinding.etJumlahBayar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val bayar = s.toString().toDoubleOrNull() ?: 0.0
                val kembalian = bayar - totalFinal
                bsBinding.tvKembalian.text = if (kembalian >= 0)
                    formatRupiah.format(kembalian)
                else
                    "Kurang ${formatRupiah.format(-kembalian)}"
                bsBinding.tvKembalian.setTextColor(
                    if (kembalian >= 0)
                        getColor(com.google.android.material.R.color.design_default_color_primary)
                    else
                        getColor(android.R.color.holo_red_dark)
                )
            }
        })

        bsBinding.btnBayar.setOnClickListener {
            val uangInput = bsBinding.etJumlahBayar.text.toString().trim().toDoubleOrNull() ?: 0.0
            if (uangInput < totalFinal) {
                Toast.makeText(this, "Pembayaran kurang!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val metode = when (bsBinding.chipGroupMetode.checkedChipId) {
                bsBinding.chipTransfer.id -> "Transfer"
                bsBinding.chipQris.id -> "QRIS"
                else -> "Tunai"
            }

            prosesSimpanTransaksi(produkAktif, subtotal, pajak, totalFinal, uangInput, metode, dialog)
        }

        dialog.show()
    }

    private fun prosesSimpanTransaksi(
        produkAktif: List<ModelProduk>,
        subtotal: Double,
        pajak: Double,
        totalFinal: Double,
        uangInput: Double,
        metode: String,
        dialog: BottomSheetDialog
    ) {
        val notaId = "TRX${System.currentTimeMillis().toString().takeLast(6)}"
        val sdfTanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdfJam = SimpleDateFormat("HH:mm", Locale.getDefault())

        val listItem = produkAktif.map { produk ->
            ModelKeranjang(
                idProduk = produk.idProduk,
                namaProduk = produk.namaProduk,
                hargaProduk = produk.hargaProduk,
                jumlahBeli = produk.qty,
                totalHargaItem = produk.hargaProduk * produk.qty
            )
        }

        val transaksiSelesai = ModelTransaksi(
            kodeTransaksi = notaId,
            tanggal = sdfTanggal.format(Date()),
            jam = sdfJam.format(Date()),
            metodePembayaran = metode,
            subtotal = subtotal,
            pajak = pajak,
            totalBayar = totalFinal,
            jumlahUangBayar = uangInput,
            kembalian = uangInput - totalFinal,
            listItem = listItem
        )

        val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
            .getReference("Transaksi")

        dbRef.child(notaId).setValue(transaksiSelesai)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startActivity(Intent(this, DetailTransaksiActivity::class.java).apply {
                    putExtra("DATA_NOTA", transaksiSelesai)
                })
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}