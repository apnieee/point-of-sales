package com.apni.pos.transaksi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.R
import com.apni.pos.adapter.AdapterKeranjang
import com.apni.pos.adapter.AdapterProdukTransaksi
import com.apni.pos.databinding.ActivityTransaksiBinding
import com.apni.pos.databinding.BottomsheetCheckoutBinding
import com.apni.pos.databinding.BottomsheetKeranjangBinding
import com.apni.pos.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var adapterProduk: AdapterProdukTransaksi

    private var listMasterSeblak = mutableListOf<ModelProduk>()
    private var listKategoriAktif = mutableListOf<ModelKategori>()
    private var idKategoriDipilih: String? = null // null = Semua

    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        ambilDataProdukFirebase()

        binding.btnCheckout.setOnClickListener { bukaBottomSheetKeranjang() }
        binding.ivKembali.setOnClickListener { finish() }
    }

    private fun ambilDataProdukFirebase() {
        val db = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")

        db.getReference("Kategori").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKategoriAktif.clear()
                snapshot.children.forEach {
                    val kat = it.getValue(ModelKategori::class.java)
                    if (kat != null && kat.statusKategori == "Aktif") {
                        listKategoriAktif.add(kat)
                    }
                }

                setupChipKategori()

                val idKategoriList = listKategoriAktif.map { it.idKategori }

                db.getReference("Produk").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(pSnapshot: DataSnapshot) {
                        listMasterSeblak.clear()
                        pSnapshot.children.forEach { data ->
                            val produk = data.getValue(ModelProduk::class.java)
                            if (produk != null &&
                                produk.statusProduk == "Aktif" &&
                                idKategoriList.contains(produk.idKategori)) {
                                produk.qty = 0
                                listMasterSeblak.add(produk)
                            }
                        }
                        Log.d("DEBUG_TRX", "Total produk lolos filter: ${listMasterSeblak.size}")
                        filterDanTampilProduk()
                    }
                    override fun onCancelled(e: DatabaseError) {}
                })
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    private fun setupChipKategori() {
        val layoutKategori = binding.layoutKategori
        if (layoutKategori.childCount > 1) {
            layoutKategori.removeViews(1, layoutKategori.childCount - 1)
        }

        val chipSemua = binding.chipSemua
        chipSemua.setBackgroundResource(R.drawable.bg_chip_aktif)
        chipSemua.setOnClickListener {
            idKategoriDipilih = null
            setChipAktif(chipSemua)
            filterDanTampilProduk()
        }

        listKategoriAktif.forEach { kategori ->
            val chip = TextView(this).apply {
                text = kategori.namaKategori
                textSize = 13f
                setTextColor(ContextCompat.getColor(this@TransaksiActivity, android.R.color.black))
                setBackgroundResource(R.drawable.bg_chip_inaktif)
                setPadding(
                    dpToPx(16), dpToPx(6),
                    dpToPx(16), dpToPx(6)
                )
                val lp = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.marginEnd = dpToPx(8)
                layoutParams = lp

                setOnClickListener {
                    idKategoriDipilih = kategori.idKategori
                    setChipAktif(this)
                    filterDanTampilProduk()
                }
            }
            layoutKategori.addView(chip)
        }
    }

    private fun setChipAktif(chipAktif: TextView) {
        val layoutKategori = binding.layoutKategori
        for (i in 0 until layoutKategori.childCount) {
            val chip = layoutKategori.getChildAt(i) as? TextView
            chip?.setBackgroundResource(R.drawable.bg_chip_inaktif)
            chip?.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
        chipAktif.setBackgroundResource(R.drawable.bg_chip_aktif)
        chipAktif.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    private fun filterDanTampilProduk() {
        val listFiltered = if (idKategoriDipilih == null) {
            listMasterSeblak
        } else {
            listMasterSeblak.filter { it.idKategori == idKategoriDipilih }
        }
        adapterProduk.updateData(listFiltered)
        hitungTotalHalamanUtama()
    }

    private fun setupRecyclerView() {
        adapterProduk = AdapterProdukTransaksi(mutableListOf()) { hitungTotalHalamanUtama() }
        binding.rvProduk.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduk.adapter = adapterProduk
    }

    private fun hitungTotalHalamanUtama() {
        var totalItem = 0
        var totalHarga = 0.0
        listMasterSeblak.filter { it.qty > 0 }.forEach {
            totalItem += it.qty
            totalHarga += it.hargaProduk * it.qty.toDouble()
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

        val adapterKeranjang = AdapterKeranjang(produkAktif) {
            produkAktif = listMasterSeblak.filter { it.qty > 0 }.toMutableList()
            hitungTotalHalamanUtama()
            val total = produkAktif.sumOf { it.hargaProduk * it.qty.toDouble() }
            bsBinding.tvSubtotal.text = formatRupiah.format(total)
            bsBinding.tvJumlahItem.text = "${produkAktif.sumOf { it.qty }} item"
            if (produkAktif.isEmpty()) dialog.dismiss()
        }

        bsBinding.rvKeranjang.layoutManager = LinearLayoutManager(this)
        bsBinding.rvKeranjang.adapter = adapterKeranjang
        bsBinding.tvSubtotal.text = formatRupiah.format(
            produkAktif.sumOf { it.hargaProduk * it.qty.toDouble() }
        )
        bsBinding.tvJumlahItem.text = "${produkAktif.sumOf { it.qty }} item"

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

        val subtotal = produkAktif.sumOf { it.hargaProduk * it.qty.toDouble() }
        val pajak = subtotal * 0.10
        val totalFinal = subtotal + pajak

        bsBinding.tvCheckoutSubtotal.text = formatRupiah.format(subtotal)
        bsBinding.tvCheckoutPajak.text = formatRupiah.format(pajak)
        bsBinding.tvCheckoutTotal.text = formatRupiah.format(totalFinal)

        bsBinding.rvDetailPesanan.layoutManager = LinearLayoutManager(this)
        bsBinding.rvDetailPesanan.adapter = AdapterKeranjang(produkAktif.toMutableList()) {}

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
                    getColor(
                        if (kembalian >= 0) com.google.android.material.R.color.design_default_color_primary
                        else android.R.color.holo_red_dark
                    )
                )
            }
        })

        bsBinding.btnBayar.setOnClickListener {
            val uangInput = bsBinding.etJumlahBayar.text.toString().toDoubleOrNull() ?: 0.0
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

        val listItem = produkAktif.map {
            ModelKeranjang(
                idProduk = it.idProduk,
                namaProduk = it.namaProduk,
                hargaProduk = it.hargaProduk,
                jumlahBeli = it.qty,
                totalHargaItem = it.hargaProduk * it.qty.toDouble()
            )
        }

        val transaksiSelesai = ModelTransaksi(
            kodeTransaksi = notaId,
            tanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            jam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            metodePembayaran = metode,
            subtotal = subtotal,
            pajak = pajak,
            totalBayar = totalFinal,
            jumlahUangBayar = uangInput,
            kembalian = uangInput - totalFinal,
            listItem = listItem
        )

        FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
            .getReference("Transaksi")
            .child(notaId)
            .setValue(transaksiSelesai)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startActivity(
                    Intent(this, DetailTransaksiActivity::class.java).apply {
                        putExtra("DATA_NOTA", transaksiSelesai)
                    }
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}