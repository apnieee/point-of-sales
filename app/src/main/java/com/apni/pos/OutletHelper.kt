package com.apni.pos.utils

import com.apni.pos.model.ModelOutlet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object OutletHelper {
    fun ambilSemuaOutlet(onResult: (List<ModelOutlet>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Outlet")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ModelOutlet>()
                for (data in snapshot.children) {
                    val outlet = data.getValue(ModelOutlet::class.java)
                    outlet?.let { list.add(it) }
                }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }
}