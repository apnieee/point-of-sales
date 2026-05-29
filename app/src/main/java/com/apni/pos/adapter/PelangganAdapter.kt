package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.model.ModelPelanggan

class PelangganAdapter(
    private var listPelanggan: List<ModelPelanggan>,
    private val onItemClick: (ModelPelanggan) -> Unit
) : RecyclerView.Adapter<PelangganAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvItemNamaPelanggan)
        val tvHp: TextView = view.findViewById(R.id.tvItemHpPelanggan)
        val tvPoin: TextView = view.findViewById(R.id.tvItemPoin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        holder.tvNama.text = pelanggan.namaPelanggan
        holder.tvHp.text = pelanggan.nomorHp
        holder.tvPoin.text = "${pelanggan.poin} Poin"

        holder.itemView.setOnClickListener {
            onItemClick(pelanggan)
        }
    }

    override fun getItemCount(): Int = listPelanggan.size

    fun updateData(newList: List<ModelPelanggan>) {
        this.listPelanggan = newList
        notifyDataSetChanged()
    }
}