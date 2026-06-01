package com.apni.pos.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.karyawan.ModKaryawanActivity
import com.apni.pos.model.ModelKaryawan
import com.google.firebase.database.FirebaseDatabase

class KaryawanAdapter(private var listKaryawan: List<ModelKaryawan>,
                      private val onMenuClick: (ModelKaryawan, String) -> Unit) :
    RecyclerView.Adapter<KaryawanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaKaryawan)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvOutlet: TextView = view.findViewById(R.id.tvOutlet)
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
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

        holder.tvStatus.text = if (karyawan.isAktif) "Aktif" else "Non-Aktif"
        holder.tvStatus.setTextColor(if (karyawan.isAktif) android.graphics.Color.GREEN else android.graphics.Color.RED)

        when (karyawan.role.lowercase()) {
            "owner" -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
            "admin" -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#1976D2"))
            else -> holder.tvRole.setTextColor(android.graphics.Color.parseColor("#AE4A0B"))
        }

        holder.ivMenu.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(view.context, view)
            popup.menu.add("Edit")
            popup.menu.add("Hapus")
            popup.setOnMenuItemClickListener { item ->
                when(item.title) {
                    "Edit" -> {
                        val intent = Intent(view.context, ModKaryawanActivity::class.java)
                        intent.putExtra("DATA_KARYAWAN", karyawan) // Kirim object karyawan
                        view.context.startActivity(intent)
                    }
                    "Hapus" -> {
                        // Hapus dari Firebase
                        FirebaseDatabase.getInstance().getReference("Karyawan")
                            .child(karyawan.idKaryawan).removeValue()
                    }
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = listKaryawan.size

    fun updateData(newList: List<ModelKaryawan>) {
        this.listKaryawan = newList
        notifyDataSetChanged()
    }
}