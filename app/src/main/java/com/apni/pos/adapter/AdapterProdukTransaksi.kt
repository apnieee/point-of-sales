package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemDataTransaksiBinding
import com.apni.pos.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class AdapterProdukTransaksi(
    private val listProduk: MutableList<ModelProduk> = mutableListOf(),
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<AdapterProdukTransaksi.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDataTransaksiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDataTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }

        holder.binding.apply {
            tvNamaProduk.text = produk.namaProduk
            tvHarga.text = formatRupiah.format(produk.hargaProduk)

            tvDiskon.visibility = View.GONE

            if (produk.qty == 0) {
                btnTambah.visibility = View.VISIBLE
                layoutQty.visibility = View.GONE
            } else {
                btnTambah.visibility = View.GONE
                layoutQty.visibility = View.VISIBLE
                tvQty.text = produk.qty.toString()
            }

            btnTambah.setOnClickListener {
                produk.qty = 1
                notifyItemChanged(holder.adapterPosition)
                onCartChanged()
            }

            btnTambahQty.setOnClickListener {
                produk.qty++
                tvQty.text = produk.qty.toString()
                onCartChanged()
            }

            btnKurang.setOnClickListener {
                if (produk.qty > 0) {
                    produk.qty--
                    if (produk.qty == 0) {
                        notifyItemChanged(holder.adapterPosition)
                    } else {
                        tvQty.text = produk.qty.toString()
                    }
                    onCartChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<ModelProduk>) {
        this.listProduk.clear()
        this.listProduk.addAll(newList)
        notifyDataSetChanged()
    }
}