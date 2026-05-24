package com.apni.pos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.kategori.DataKategoriActivity
import com.apni.pos.transaksi.TransaksiActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvSelamat: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvEstimasi: TextView

    private lateinit var ivTransaksi: ImageView
    private lateinit var ivLaporan: ImageView

    private lateinit var cardAkun: CardView
    private lateinit var cardProduk: CardView
    private lateinit var cardKategori: CardView
    private lateinit var cardPegawai: CardView
    private lateinit var cardCabang: CardView
    private lateinit var cardPrinter: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setupTanggal()
        setupListeners()
    }

    private fun init() {
        tvSelamat   = findViewById(R.id.tvSelamat)
        tvTanggal   = findViewById(R.id.tvTanggal)
        tvEstimasi  = findViewById(R.id.tvEstimasi)

        ivTransaksi = findViewById(R.id.ivTransaksi)
        ivLaporan = findViewById(R.id.ivLaporan)

        cardAkun     = findViewById(R.id.cardAkun)
        cardProduk  = findViewById(R.id.cardProduk)
        cardKategori = findViewById(R.id.cardKategori)
        cardPegawai  = findViewById(R.id.cardPegawai)
        cardCabang   = findViewById(R.id.cardCabang)
        cardPrinter  = findViewById(R.id.cardPrinter)
    }

    private fun setupTanggal() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val tanggalSekarang = dateFormat.format(Date())
        tvTanggal.text = tanggalSekarang
    }

    private fun setupListeners() {
        cardAkun.setOnClickListener {
            // startActivity(Intent(this, AkunActivity::class.java))
        }

        cardProduk.setOnClickListener {
            // startActivity(Intent(this, ProdukActivity::class.java))
        }

        cardKategori.setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }

        cardPegawai.setOnClickListener {
            // startActivity(Intent(this, PegawaiActivity::class.java))
        }

        cardCabang.setOnClickListener {
            // startActivity(Intent(this, CabangActivity::class.java))
        }

        cardPrinter.setOnClickListener {
            // startActivity(Intent(this, PrinterActivity::class.java))
        }

        ivTransaksi.setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }
    }
}