package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.model.ModelKaryawan

class KaryawanAdapter(private var listKaryawan: List<ModelKaryawan>) :
    RecyclerView.Adapter<KaryawanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaKaryawan)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvOutlet: TextView = view.findViewById(R.id.tvOutlet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_karyawan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val karyawan = listKaryawan[position]

        holder.tvNama.text = karyawan.namaKaryawan
        holder.tvUsername.text = karyawan.nomorHp
        holder.tvRole.text = karyawan.role
        holder.tvOutlet.text = "Outlet Pusat"

        when (karyawan.role.lowercase()) {
            "owner" -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
            "admin" -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#1976D2"))
            else -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#AE4A0B"))
        }
    }

    override fun getItemCount(): Int = listKaryawan.size

    fun updateData(newList: List<ModelKaryawan>) {
        this.listKaryawan = newList
        notifyDataSetChanged()
    }
}