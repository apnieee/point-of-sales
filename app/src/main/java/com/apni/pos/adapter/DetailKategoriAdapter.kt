package com.apni.pos.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.kategori.ModelKategori
import com.google.android.material.chip.Chip

class DetailKategoriAdapter (private val kategoriList: List<ModelKategori>) :
    RecyclerView.Adapter<DetailKategoriAdapter.KategoriViewHolder>() {
    lateinit var appContext: Context
    interface OnItemClickListener {
        fun onItemClick(kategori: ModelKategori)
    }
    private var listener: OnItemClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailKategoriAdapter.KategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_data_kategori, parent, false
        )
        appContext = parent.context
        return KategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val kategori = kategoriList[position]
        holder.bind(kategori)

        holder.itemView.setOnClickListener{
            listener?.onItemClick(kategori)
        }
    }

    override fun getItemCount(): Int {
        return kategoriList.size
    }

    inner class KategoriViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val tvNamaKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)

        fun bind(kategori: ModelKategori) {
            tvNamaKategori.text = kategori.namaKategori

            val status = kategori.statusKategori

            if (status == "Aktif" || status == "Tidak Aktif") {
                chipStatus.text = (appContext as Activity).getString(R.string.status_aktif)
                chipStatus.setChipBackgroundColorResource(R.color.status_active_bg)
                chipStatus.setTextColor(
                    itemView.context.getColor(R.color.status_active_text)
                )
                chipStatus.setChipIconResource(R.drawable.check)
                chipStatus.setChipIconTintResource(R.color.status_active_text)
            } else {
                chipStatus.text = (appContext as Activity).getString(R.string.status_nonaktif)
            }
        }
    }
}