package com.apni.pos.transaksi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.apni.pos.databinding.ActivityDataTransaksiBinding
import com.google.firebase.database.*

class TransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataTransaksiBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: TransaksiAdapter

    private var list = ArrayList<ModelTransaksi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityDataTransaksiBinding.inflate(layoutInflater)

        setContentView(binding.root)

        database =
            FirebaseDatabase.getInstance()
                .getReference("transactions")

        binding.rvTransaksi.layoutManager =
            LinearLayoutManager(this)

        adapter = TransaksiAdapter(this, list)

        binding.rvTransaksi.adapter = adapter

        getData()

        binding.fabTambah.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ModTransaksiActivity::class.java
                )
            )
        }
    }

    private fun getData() {

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()

                    for (dataSnapshot in snapshot.children) {

                        val transaksi =
                            dataSnapshot.getValue(
                                ModelTransaksi::class.java
                            )

                        transaksi?.let {
                            list.add(it)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
    }
}