package com.apni.pos

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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

        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        binding.tvUsername.text = username

        db.collection("pegawai").document(username)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nama = document.getString("nama") ?: username
                    val jabatan = document.getString("jabatan") ?: "-"

                    binding.tvNamaPegawai.text = nama
                    binding.tvJabatan.text = jabatan
                    binding.tvJabatanDetail.text = jabatan

                    // Avatar inisial
                    val inisial = nama.split(" ").take(2)
                        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                    binding.tvAvatar.text = inisial

                    val avatarColors = listOf(
                        "#1565C0", "#6A1B9A", "#E65100", "#00695C", "#AD1457"
                    )
                    val warna = avatarColors[nama.length % avatarColors.size]
                    (binding.tvAvatar.background as? GradientDrawable)
                        ?.setColor(Color.parseColor(warna))
                }
            }

        binding.ivKembali.setOnClickListener { finish() }

        binding.btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}