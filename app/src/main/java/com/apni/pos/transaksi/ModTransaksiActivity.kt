package com.apni.pos.transaksi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityModTransaksiBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpanTransaksi.setOnClickListener {
            simpanData()
        }
    }

    private fun simpanData() {

        val database =
            FirebaseDatabase.getInstance()
                .getReference("transactions")

        val id = database.push().key ?: return

        val jam =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())

        val transaksi = ModelTransaksi(
            id = id,
            kode = "TRX-${System.currentTimeMillis()}",
            total = binding.tvTotal.text.toString(),
            jam = jam,
            status = binding.autoStatus.text.toString(),
            detail = "Seblak Original"
        )

        database.child(id)
            .setValue(transaksi)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Transaksi berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
    }
}