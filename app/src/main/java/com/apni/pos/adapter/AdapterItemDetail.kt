package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemPesananBinding
import com.apni.pos.model.ModelKeranjang
import java.text.NumberFormat
import java.util.Locale

class AdapterItemDetail(private val listItem: List<ModelKeranjang>) :
    RecyclerView.Adapter<AdapterItemDetail.ViewHolder>() {

    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    class ViewHolder(val binding: ItemPesananBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItem[position]

        holder.binding.apply {
            tvNamaProduk.text = item.namaProduk
            val detailTeks = "${item.jumlahBeli} x ${formatRupiah.format(item.hargaProduk)}"
            tvDetailPesanan.text = detailTeks
        }
    }

    override fun getItemCount(): Int = listItem.size
}