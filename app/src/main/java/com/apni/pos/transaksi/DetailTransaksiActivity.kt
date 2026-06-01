package com.apni.pos.transaksi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apni.pos.databinding.LayoutDetailTransaksiBinding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import java.text.NumberFormat
import java.util.Locale
import android.content.Intent
import android.net.Uri
import com.apni.pos.model.ModelTransaksi

class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDetailTransaksiBinding
    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nota = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DATA_NOTA", ModelTransaksi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ModelTransaksi>("DATA_NOTA")
        }


        nota?.let { dataNota ->
            binding.apply {
                tvTotal.text = formatRupiah.format(dataNota.totalBayar)
                tvStatus.text = "SUKSES"
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#0E7C2B"))

                tvKode.text = dataNota.kodeTransaksi
                tvTanggal.text = dataNota.tanggal
                tvJam.text = dataNota.jam
                tvPembayaran.text = dataNota.metodePembayaran
                tvPesanan.text = dataNota.detailPesananTeks

                btnBack.setOnClickListener { finish() }
                btnEdit.setOnClickListener { finish() }

                btnPrint.setOnClickListener {
                    periksaIzinDanCetak(dataNota)
                }
            }
        }
    }

    private fun periksaIzinDanCetak(nota: ModelTransaksi) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 101)
                return
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
                return
            }
        }
        aksiCetakBluetooth(nota)
    }


    private fun aksiCetakBluetooth(nota: ModelTransaksi) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin Bluetooth tidak diberikan", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            val bluetoothConnections = BluetoothPrintersConnections()
            val listPrinter = bluetoothConnections.list

            if (listPrinter != null && listPrinter.isNotEmpty()) {
                val printer = EscPosPrinter(listPrinter[0], 203, 48f, 32)

                var kontenStruk = "[C]<b>SEBLAK PRASMANAN</b>\n" +
                        "[C]<b>APNI POS OUTLET</b>\n" +
                        "[C]--------------------------------\n" +
                        "[L]Nota  : ${nota.kodeTransaksi}\n" +
                        "[L]Waktu : ${nota.tanggal} ${nota.jam}\n" +
                        "[C]--------------------------------\n" +
                        "[L]<b>Detail Menu:</b>\n"

                val daftarItem = nota.detailPesananTeks.trim().split("\n")
                for (item in daftarItem) {
                    if (item.isNotEmpty()) {
                        kontenStruk += "[L]$item\n"
                    }
                }


                printer.printFormattedText(kontenStruk)
                Toast.makeText(this, "Struk berhasil dicetak!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Printer tidak ditemukan! Pastikan sudah di-pair.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}