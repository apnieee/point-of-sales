package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemRiwayatTransaksiBinding
import com.apni.pos.model.ModelTransaksi
import java.text.NumberFormat
import java.util.Locale

class RiwayatAdapter(
    private var listTransaksi: List<ModelTransaksi>,
    private val onItemClick: (ModelTransaksi) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRiwayatTransaksiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trx = listTransaksi[position]
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }

        holder.binding.apply {
            tvKodeTrx.text = trx.kodeTransaksi
            tvWaktuTrx.text = "${trx.tanggal} - ${trx.jam}"
            tvMetodeBayar.text = trx.metodePembayaran.uppercase()
            tvNominalTrx.text = formatRupiah.format(trx.totalBayar)

            root.setOnClickListener {
                onItemClick(trx)
            }
        }
    }

    override fun getItemCount(): Int = listTransaksi.size

    fun updateData(newList: List<ModelTransaksi>) {
        this.listTransaksi = newList
        notifyDataSetChanged()
    }
}