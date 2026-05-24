package com.apni.pos.transaksi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.apni.pos.databinding.LayoutDetailTransaksiBinding

class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDetailTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kode = intent.getStringExtra("kode") ?: "-"
        val total = intent.getStringExtra("total") ?: "0"
        val jam = intent.getStringExtra("jam") ?: "-"
        val status = intent.getStringExtra("status") ?: "Unknown"
        val detail = intent.getStringExtra("detail") ?: "-"
        val tanggal = intent.getStringExtra("tanggal") ?: "-"
        val pembayaran = intent.getStringExtra("pembayaran") ?: "-"

        binding.tvKode.text = kode
        binding.tvTotal.text = "Rp $total"
        binding.tvJam.text = jam
        binding.tvPesanan.text = detail
        binding.tvTanggal.text = tanggal
        binding.tvPembayaran.text = pembayaran

        binding.tvStatus.text = status.uppercase()

        when (status) {
            "Lunas" -> binding.tvStatus.setBackgroundResource(R.drawable.bg_status_lunas)
            "Refund" -> binding.tvStatus.setBackgroundResource(R.drawable.bg_status_refund)
            else -> binding.tvStatus.setBackgroundResource(R.drawable.bg_status_batal)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEdit.setOnClickListener {

        }

        binding.btnPrint.setOnClickListener {

        }
    }
}