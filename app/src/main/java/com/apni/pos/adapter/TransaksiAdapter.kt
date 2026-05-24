package com.apni.pos.transaksi

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemDataTransaksiBinding

class TransaksiAdapter(
    private val context: Context,
    private val list: ArrayList<ModelTransaksi>
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDataTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemDataTransaksiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]

        holder.binding.tvQty.text = "${position + 1}"
        holder.binding.tvNamaMenu.text = data.kode
        holder.binding.tvDetail.text = data.detail
        holder.binding.tvHarga.text = data.total

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, DetailTransaksiActivity::class.java)

            intent.putExtra("kode", data.kode)
            intent.putExtra("total", data.total)
            intent.putExtra("jam", data.jam)
            intent.putExtra("status", data.status)
            intent.putExtra("detail", data.detail)
            intent.putExtra("tanggal", data.tanggal)
            intent.putExtra("pembayaran", data.pembayaran)

            holder.itemView.context.startActivity(intent)
        }
    }
}