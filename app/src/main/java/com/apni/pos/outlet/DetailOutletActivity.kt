package com.apni.pos.outlet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityDetailOutletBinding
import com.apni.pos.model.ModelOutlet

class DetailOutletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailOutletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val outlet = intent.getParcelableExtra<ModelOutlet>("OUTLET")

        outlet?.let { data ->
            binding.tvNamaOutletDetail.text = data.namaOutlet
            binding.tvAlamatDetail.text = "Alamat: ${data.alamatOutlet}"
            binding.tvKontakDetail.text = "No. Kontak: ${data.nomorOutlet}"

            binding.btnEditOutlet.setOnClickListener {
                val intent = Intent(this, ModOutletActivity::class.java)
                intent.putExtra("DATA_OUTLET", data)
                startActivity(intent)
                finish()
            }
        }

        binding.ivkembali.setOnClickListener { finish() }
    }
}