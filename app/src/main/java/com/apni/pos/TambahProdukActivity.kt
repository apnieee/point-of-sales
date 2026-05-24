package com.apni.pos.produk

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var imgProduk: ImageView
    private lateinit var btnKamera: MaterialButton
    private lateinit var btnGaleri: MaterialButton

    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var etSKU: TextInputEditText
    private lateinit var etBarcode: TextInputEditText

    private lateinit var etHargaBeli: TextInputEditText
    private lateinit var spinnerTipeKeuntungan: Spinner
    private lateinit var etNilaiProfit: TextInputEditText
    private lateinit var etHargaJual: TextInputEditText

    private lateinit var etStok: TextInputEditText
    private lateinit var cbStok: CheckBox

    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mod_produk)

        database = FirebaseDatabase
            .getInstance()
            .getReference("produk")

        init()
        setupSpinner()
        setupListener()
    }

    private fun init() {

        imgProduk = findViewById(R.id.imgProduk)

        btnKamera = findViewById(R.id.btnKamera)
        btnGaleri = findViewById(R.id.btnGaleri)

        etNamaProduk = findViewById(R.id.etNamaKategori)
        etSKU = findViewById(R.id.etSKU)
        etBarcode = findViewById(R.id.etBarcode)

        etHargaBeli = findViewById(R.id.etHargaBeli)
        spinnerTipeKeuntungan =
            findViewById(R.id.spinnerTipeKeuntungan)

        etNilaiProfit = findViewById(R.id.etNilaiProfit)
        etHargaJual = findViewById(R.id.etHargaJual)

        etStok = findViewById(R.id.etStok)
        cbStok = findViewById(R.id.cbStok)

        btnSimpan = findViewById(R.id.btnSimpan)
    }

    private fun setupSpinner() {

        val listProfit = arrayOf(
            "Profit Rupiah",
            "Profit Persen"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listProfit
        )

        spinnerTipeKeuntungan.adapter = adapter
    }

    private fun setupListener() {

        btnKamera.setOnClickListener {

            Toast.makeText(
                this,
                "Buka Kamera",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnGaleri.setOnClickListener {

            Toast.makeText(
                this,
                "Buka Galeri",
                Toast.LENGTH_SHORT
            ).show()
        }

        cbStok.setOnCheckedChangeListener { _, isChecked ->

            etStok.isEnabled = !isChecked

            if (isChecked) {
                etStok.setText("Tak Terbatas")
            } else {
                etStok.setText("")
            }
        }

        btnSimpan.setOnClickListener {

            hitungHargaJual()
            simpanProduk()
        }
    }

    private fun hitungHargaJual() {

        val hargaBeli =
            etHargaBeli.text.toString().toDoubleOrNull() ?: 0.0

        val profit =
            etNilaiProfit.text.toString().toDoubleOrNull() ?: 0.0

        val tipeProfit =
            spinnerTipeKeuntungan.selectedItem.toString()

        val hargaJual: Double

        hargaJual = if (tipeProfit == "Profit Rupiah") {

            hargaBeli + profit

        } else {

            hargaBeli + (hargaBeli * profit / 100)
        }

        etHargaJual.setText(
            hargaJual.toInt().toString()
        )
    }

    private fun simpanProduk() {

        val idProduk = database.push().key!!

        val produkActivity = ProdukActivity(

            etNamaProduk.text.toString(),
            etSKU.text.toString(),
            etBarcode.text.toString(),
            etHargaBeli.text.toString(),
            etHargaJual.text.toString(),
            etStok.text.toString()
        )

        database.child(idProduk)
            .setValue(produkActivity)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Produk berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal menyimpan produk",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}