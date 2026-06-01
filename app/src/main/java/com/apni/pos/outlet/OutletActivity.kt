package com.apni.pos.outlet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.adapter.OutletAdapter
import com.apni.pos.databinding.ActivityOutletBinding
import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutletBinding
    private lateinit var adapter: OutletAdapter
    private val listOutlet = mutableListOf<ModelOutlet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        ambilDataOutlet()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, ModOutletActivity::class.java))
        }

        binding.ivkembali.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = OutletAdapter(listOutlet)
        binding.rvOutlet.layoutManager = LinearLayoutManager(this)
        binding.rvOutlet.adapter = adapter
    }

    private fun ambilDataOutlet() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Outlet")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOutlet.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val outlet = data.getValue(ModelOutlet::class.java)
                        outlet?.let { listOutlet.add(it) }
                    }
                }
                adapter.updateData(listOutlet)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OutletActivity, "Gagal memuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}