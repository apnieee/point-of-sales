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
    private val onClick: (ModelKategori) -> Unit,
    private val onStatusClick: (ModelKategori) -> Unit,
    private val onDeleteClick: (ModelKategori) -> Unit
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

            // Logika Warna
            val isAktif = kategori.statusKategori.equals("Aktif", ignoreCase = true)
            val warna = if (isAktif) Color.GREEN else Color.GRAY
            chipStatus.chipBackgroundColor = ColorStateList.valueOf(warna)

            // Klik chip untuk ubah status
            chipStatus.setOnClickListener { onStatusClick(kategori) }

            // Tambahkan tombol hapus (pastikan ada ID btnHapus di XML item)
            // Jika belum ada, tambahkan ImageView/Button hapus di item_data_kategori.xml
            holder.binding.btnHapus.setOnClickListener { onDeleteClick(kategori) }

            root.setOnClickListener { onClick(kategori) }
        }
    }

    override fun getItemCount(): Int = listKategori.size

    fun updateData(newList: List<ModelKategori>) {
        listKategori = newList
        notifyDataSetChanged()
    }
}