package com.apni.pos.transaksi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.adapter.AdapterItemDetail
import com.apni.pos.databinding.LayoutDetailTransaksiBinding
import com.apni.pos.model.ModelTransaksi
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDetailTransaksiBinding
    private lateinit var nota: ModelTransaksi
    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nota = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DATA_NOTA", ModelTransaksi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("DATA_NOTA")
        } ?: run { finish(); return }

        tampilkanData()
        setupTombol()
    }

    private fun tampilkanData() {
        binding.apply {
            tvTotal.text = formatRupiah.format(nota.totalBayar)
            tvStatus.text = "SUKSES"
            tvStatus.setBackgroundColor(Color.parseColor("#0E7C2B"))
            tvKode.text = nota.kodeTransaksi
            tvTanggal.text = nota.tanggal
            tvJam.text = nota.jam
            tvPembayaran.text = nota.metodePembayaran
            tvKembalian.text = formatRupiah.format(nota.kembalian)
            tvDetailSubtotal.text = formatRupiah.format(nota.subtotal)
            tvDetailPajak.text = formatRupiah.format(nota.pajak)
            tvDetailTotal.text = formatRupiah.format(nota.totalBayar)

            rvDetailPesananFinal.layoutManager = LinearLayoutManager(this@DetailTransaksiActivity)
            rvDetailPesananFinal.adapter = AdapterItemDetail(nota.listItem)
        }
    }

    private fun setupTombol() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrint.setOnClickListener { periksaIzinDanCetak() }
        binding.btnSharePng.setOnClickListener { shareAsPng() }
        binding.btnSharePdf.setOnClickListener { shareAsPdf() }
    }

    private fun periksaIzinDanCetak() {
        val izin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            Manifest.permission.BLUETOOTH_CONNECT
        else
            Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, izin) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(izin), 101)
        } else {
            aksiCetakBluetooth()
        }
    }

    private fun aksiCetakBluetooth() {
        try {
            val listPrinter = BluetoothPrintersConnections().list
            if (listPrinter.isNullOrEmpty()) {
                Toast.makeText(this, "Printer tidak ditemukan!", Toast.LENGTH_SHORT).show()
                return
            }

            val printer = EscPosPrinter(listPrinter[0], 203, 48f, 32)
            var konten = "[C]<b>SEBLAK PRASMANAN</b>\n" +
                    "[C]Jl. Contoh No. 1, Kota\n" +
                    "[C]--------------------------------\n" +
                    "[L]Nota  : ${nota.kodeTransaksi}\n" +
                    "[L]Tgl   : ${nota.tanggal} ${nota.jam}\n" +
                    "[L]Bayar : ${nota.metodePembayaran}\n" +
                    "[C]--------------------------------\n"

            nota.listItem.forEach {
                val subtotalItem = formatRupiah.format(it.totalHargaItem)
                konten += "[L]${it.namaProduk}[R]${subtotalItem}\n"
                konten += "[L]  ${it.jumlahBeli} x ${formatRupiah.format(it.hargaProduk)}\n"
            }

            konten += "[C]--------------------------------\n" +
                    "[L]Subtotal[R]${formatRupiah.format(nota.subtotal)}\n" +
                    "[L]Pajak 10%[R]${formatRupiah.format(nota.pajak)}\n" +
                    "[L]<b>TOTAL[R]${formatRupiah.format(nota.totalBayar)}</b>\n" +
                    "[L]Bayar[R]${formatRupiah.format(nota.jumlahUangBayar)}\n" +
                    "[L]<b>Kembali[R]${formatRupiah.format(nota.kembalian)}</b>\n" +
                    "[C]--------------------------------\n" +
                    "[C]Terima kasih!\n" +
                    "[C]Selamat menikmati :)\n\n"

            printer.printFormattedText(konten)
            Toast.makeText(this, "Struk berhasil dicetak!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareAsPng() {
        try {
            val view = binding.root
            Log.d("DEBUG_SHARE", "View width: ${view.width}, height: ${view.height}")

            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            Log.d("DEBUG_SHARE", "Bitmap berhasil dibuat")

            val file = File(cacheDir, "struk_${nota.kodeTransaksi}.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            Log.d("DEBUG_SHARE", "File PNG disimpan di: ${file.absolutePath}")

            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            Log.d("DEBUG_SHARE", "URI: $uri")

            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Bagikan Struk via"))

        } catch (e: Exception) {
            Log.e("DEBUG_SHARE", "Error PNG: ${e.message}", e)
            Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareAsPdf() {
        try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply { textSize = 14f; color = Color.BLACK }
            val paintBold = Paint().apply { textSize = 14f; color = Color.BLACK; isFakeBoldText = true }
            val paintTitle = Paint().apply { textSize = 18f; color = Color.BLACK; isFakeBoldText = true }

            var y = 60f
            val marginL = 40f
            val marginR = 555f

            fun tulis(teks: String, p: Paint = paint, x: Float = marginL) {
                canvas.drawText(teks, x, y, p); y += 24f
            }
            fun garis() { canvas.drawLine(marginL, y, marginR, y, paint); y += 16f }
            fun tulisKananKiri(kiri: String, kanan: String, p: Paint = paint) {
                canvas.drawText(kiri, marginL, y, p)
                canvas.drawText(kanan, marginR - paint.measureText(kanan), y, p)
                y += 24f
            }

            tulis("KATANYA SIH SEBLAK", paintTitle, (595 - paintTitle.measureText("KATANYA SIH SEBLAK")) / 2)
            tulis("Jl. Kenangan Mantan No. 1", paint, (595 - paint.measureText("Jl. Kenangan Mantan No. 1")) / 2)
            garis()
            tulis("Nota   : ${nota.kodeTransaksi}")
            tulis("Tanggal: ${nota.tanggal}  ${nota.jam}")
            tulis("Bayar  : ${nota.metodePembayaran}")
            garis()
            nota.listItem.forEach {
                tulisKananKiri(it.namaProduk, formatRupiah.format(it.totalHargaItem))
                tulis("  ${it.jumlahBeli} x ${formatRupiah.format(it.hargaProduk)}")
            }
            garis()
            tulisKananKiri("Subtotal", formatRupiah.format(nota.subtotal))
            tulisKananKiri("Pajak 10%", formatRupiah.format(nota.pajak))
            tulisKananKiri("TOTAL", formatRupiah.format(nota.totalBayar), paintBold)
            tulisKananKiri("Bayar", formatRupiah.format(nota.jumlahUangBayar))
            tulisKananKiri("Kembali", formatRupiah.format(nota.kembalian), paintBold)
            garis()
            tulis("Terima kasih! Selamat menikmati seporsi kemenangan!", paint,
                (595 - paint.measureText("Terima kasih! Selamat menikmati seporsi kemenangan!")) / 2)

            pdfDoc.finishPage(page)

            val file = File(cacheDir, "struk_${nota.kodeTransaksi}.pdf")
            FileOutputStream(file).use { pdfDoc.writeTo(it) }
            pdfDoc.close()

            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Bagikan PDF via"))

        } catch (e: Exception) {
            Toast.makeText(this, "Gagal buat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}