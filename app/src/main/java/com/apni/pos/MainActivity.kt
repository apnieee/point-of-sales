package com.apni.pos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.karyawan.DataKaryawanActivity
import com.apni.pos.kategori.DataKategoriActivity
import com.apni.pos.laporan.LaporanActivity
import com.apni.pos.model.ModelTransaksi
import com.apni.pos.outlet.OutletActivity
import com.apni.pos.pelanggan.DataPelangganActivity
import com.apni.pos.produk.DataProdukActivity
import com.apni.pos.transaksi.TransaksiActivity
import com.apni.pos.pelanggan.ModPelangganActivity
import com.apni.pos.printer.PrinterActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvSelamat: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvEstimasi: TextView

    private lateinit var ivTransaksi: ImageView
    private lateinit var ivPelanggan: ImageView
    private lateinit var ivLaporan: ImageView

    private lateinit var cardAkun: CardView
    private lateinit var cardProduk: CardView
    private lateinit var cardKategori: CardView
    private lateinit var cardPegawai: CardView
    private lateinit var cardCabang: CardView
    private lateinit var cardPrinter: CardView

    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setupTanggal()
        setupListeners()

        hitungEstimasiHariIni()
    }

    private fun init() {
        tvSelamat   = findViewById(R.id.tvSelamat)
        tvTanggal   = findViewById(R.id.tvTanggal)
        tvEstimasi  = findViewById(R.id.tvEstimasi)

        ivTransaksi = findViewById(R.id.ivTransaksi)
        ivPelanggan = findViewById(R.id.ivPelanggan)
        ivLaporan   = findViewById(R.id.ivLaporan)

        cardAkun     = findViewById(R.id.cardAkun)
        cardProduk   = findViewById(R.id.cardProduk)
        cardKategori = findViewById(R.id.cardKategori)
        cardPegawai  = findViewById(R.id.cardPegawai)
        cardCabang   = findViewById(R.id.cardCabang)
        cardPrinter  = findViewById(R.id.cardPrinter)
    }

    private fun setupTanggal() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val tanggalSekarang = dateFormat.format(Date())
        tvTanggal.text = tanggalSekarang
    }

    private fun hitungEstimasiHariIni() {
        val sdfFormatTransaksi = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val tanggalHariIni = sdfFormatTransaksi.format(Date())

        val dbTransaksiRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/")
            .getReference("Transaksi")

        dbTransaksiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPendapatan = 0.0

                for (data in snapshot.children) {
                    val transaksi = data.getValue(ModelTransaksi::class.java)

                    if (transaksi != null && transaksi.tanggal == tanggalHariIni) {
                        totalPendapatan += transaksi.totalBayar
                    }
                }

                tvEstimasi.text = formatRupiah.format(totalPendapatan).replace(",00", "")
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setupListeners() {
        cardAkun.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        cardProduk.setOnClickListener {
            startActivity(Intent(this, DataProdukActivity::class.java))
        }

        cardKategori.setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }

        cardPegawai.setOnClickListener {
            startActivity(Intent(this, DataKaryawanActivity::class.java))
        }

        cardCabang.setOnClickListener {
            startActivity(Intent(this, OutletActivity::class.java))
        }

        cardPrinter.setOnClickListener {
            startActivity(Intent(this, PrinterActivity::class.java))
        }

        ivTransaksi.setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }

        ivPelanggan.setOnClickListener {
            startActivity(Intent(this, DataPelangganActivity::class.java))
        }

        ivLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }
    }
}