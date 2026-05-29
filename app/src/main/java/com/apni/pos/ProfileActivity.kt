package com.apni.pos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apni.pos.databinding.ActivityProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil username dari session
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        // Ambil data dari Firestore
        db.collection("pegawai").document(username)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvNamaPegawai.text = document.getString("nama")
                    binding.tvJabatan.text = document.getString("jabatan")
                }
            }

        // Logika Logout
        binding.btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}