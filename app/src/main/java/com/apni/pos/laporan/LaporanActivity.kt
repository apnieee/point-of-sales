package com.apni.pos.laporan

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.adapter.RiwayatAdapter
import com.apni.pos.model.ModelTransaksi
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class LaporanActivity : AppCompatActivity() {

    private lateinit var tvTotalOmzet: TextView
    private lateinit var btnBack: ImageView
    private lateinit var rvRiwayat: RecyclerView

    private lateinit var adapter: RiwayatAdapter
    private val listTransaksi = mutableListOf<ModelTransaksi>()
    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        initComponent()
        setupRecyclerView()
        ambilDataTransaksiHariIni()

        btnBack.setOnClickListener { finish() }
    }

    private fun initComponent() {
        tvTotalOmzet = findViewById(R.id.tvTotalOmzet)
        btnBack = findViewById(R.id.btnBack)
        rvRiwayat = findViewById(R.id.rvRiwayat)
    }

    private fun setupRecyclerView() {
        adapter = RiwayatAdapter(listTransaksi) { transaksiTerpilih ->
            Toast.makeText(this, "Nota: ${transaksiTerpilih.kodeTransaksi}", Toast.LENGTH_SHORT).show()
        }
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        rvRiwayat.adapter = adapter
    }

    private fun ambilDataTransaksiHariIni() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val tanggalHariIni = sdf.format(Date())

        val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Transaksi")

        dbRef.orderByChild("tanggal").equalTo(tanggalHariIni).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalOmzet = 0.0
                listTransaksi.clear()

                for (data in snapshot.children) {
                    val trx = data.getValue(ModelTransaksi::class.java)
                    trx?.let {
                        totalOmzet += it.totalBayar
                        listTransaksi.add(it)
                    }
                }

                tvTotalOmzet.text = formatRupiah.format(totalOmzet)

                adapter.updateData(listTransaksi)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LaporanActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}