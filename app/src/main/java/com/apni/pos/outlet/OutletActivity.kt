package com.apni.pos.outlet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityOutletBinding
import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

class OutletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutletBinding
    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ambilDataLaporanOutlet()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, ModOutletActivity::class.java))
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun ambilDataLaporanOutlet() {
        val dbRef = FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").getReference("Outlet")

        dbRef.limitToFirst(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val outlet = data.getValue(ModelOutlet::class.java)
                        outlet?.let { updateTampilanLaporan(it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OutletActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTampilanLaporan(outlet: ModelOutlet) {
        val selisih = outlet.totalSistem - outlet.totalAktual

        binding.apply {
            tvOutlet.text = outlet.namaOutlet
            tvTotalSistem.text = formatRupiah.format(outlet.totalSistem)
            tvTotalAktual.text = formatRupiah.format(outlet.totalAktual)
            tvSelisih.text = "Total Selisih: ${formatRupiah.format(selisih)}"

            if (selisih == 0.0) {
                tvSelisih.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            } else {
                tvSelisih.setTextColor(android.graphics.Color.parseColor("#C62828"))
            }
        }
    }
}