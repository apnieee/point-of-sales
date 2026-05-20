package com.apni.pos.transaksi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.example.pos.TransaksiAdapter

class TransaksiActivity : AppCompatActivity() {

    private lateinit var rvTransaksi: RecyclerView
    private lateinit var transaksiAdapter: TransaksiAdapter
    private lateinit var listTransaksi: ArrayList<Transaksi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        rvTransaksi = findViewById(R.id.rvTransaksi)

        listTransaksi = arrayListOf(

            Transaksi(
                "Rp 120.000",
                "10:30",
                "TRX-001",
                "Lunas"
            ),

            Transaksi(
                "Rp 75.000",
                "11:00",
                "TRX-002",
                "Refund"
            ),

            Transaksi(
                "Rp 50.000",
                "11:45",
                "TRX-003",
                "Batal"
            )
        )

        transaksiAdapter = TransaksiAdapter(listTransaksi)

        rvTransaksi.layoutManager = LinearLayoutManager(this)
        rvTransaksi.adapter = transaksiAdapter
    }

    class Transaksi(s: String, s1: String, s2: String, s3: String) {

    }
}