package com.apni.pos.transaksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R

class TransaksiAdapter(
    private val listTransaksi: ArrayList<Transaksi>
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvJam: TextView = itemView.findViewById(R.id.tvJam)
        val tvKode: TextView = itemView.findViewById(R.id.tvKode)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaksi = listTransaksi[position]

        holder.tvHarga.text = transaksi.harga
        holder.tvJam.text = transaksi.jam
        holder.tvKode.text = transaksi.kode
        holder.tvStatus.text = transaksi.status

        when (transaksi.status) {

            "Lunas" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_lunas)
            }

            "Refund" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_refund)
            }

            "Batal" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_batal)
            }
        }
    }
}