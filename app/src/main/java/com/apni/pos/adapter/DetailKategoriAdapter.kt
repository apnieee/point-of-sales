package com.apni.pos.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemDataKategoriBinding
import com.apni.pos.model.ModelKategori

class DetailKategoriAdapter(
    private var listKategori: List<ModelKategori>,
    private val onClick: (ModelKategori) -> Unit
) : RecyclerView.Adapter<DetailKategoriAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDataKategoriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDataKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = listKategori[position]

        holder.binding.apply {
            tvKategori.text = kategori.namaKategori
            chipStatus.text = kategori.statusKategori

            if (kategori.statusKategori.equals("Aktif", ignoreCase = true)) {
                val warnaHijau = ContextCompat.getColor(root.context, android.R.color.holo_green_light)
                chipStatus.chipBackgroundColor = ColorStateList.valueOf(warnaHijau)
                chipStatus.setTextColor(Color.BLACK)
            } else {
                val warnaAbu = ContextCompat.getColor(root.context, android.R.color.darker_gray)
                chipStatus.chipBackgroundColor = ColorStateList.valueOf(warnaAbu)
                chipStatus.setTextColor(Color.WHITE)
            }

            root.setOnClickListener { onClick(kategori) }
        }
    }

    override fun getItemCount(): Int = listKategori.size

    fun updateData(newList: List<ModelKategori>) {
        listKategori = newList
        notifyDataSetChanged()
    }
}