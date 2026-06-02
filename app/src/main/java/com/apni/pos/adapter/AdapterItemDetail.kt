package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemDetailPesananBinding
import com.apni.pos.model.ModelKeranjang
import java.text.NumberFormat
import java.util.Locale

class AdapterItemDetail(
    private val listItem: List<ModelKeranjang>
) : RecyclerView.Adapter<AdapterItemDetail.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDetailPesananBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailPesananBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItem[position]
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }

        holder.binding.apply {
            tvNamaItemDetail.text = item.namaProduk
            tvQtyHargaDetail.text = "${item.jumlahBeli} x ${formatRupiah.format(item.hargaProduk)}"
            tvSubtotalItemDetail.text = formatRupiah.format(item.totalHargaItem)
        }
    }

    override fun getItemCount() = listItem.size
}