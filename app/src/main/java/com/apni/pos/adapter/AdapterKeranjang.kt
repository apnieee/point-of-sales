package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemKeranjangBinding
import com.apni.pos.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class AdapterKeranjang(
    private var listProdukAktif: List<ModelProduk>,
    private val onQtyChanged: () -> Unit
) : RecyclerView.Adapter<AdapterKeranjang.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKeranjangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKeranjangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProdukAktif[position]
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }

        holder.binding.apply {
            tvNamaProdukKeranjang.text = produk.namaProduk
            tvHargaSatuanKeranjang.text = "${formatRupiah.format(produk.hargaProduk)} / item"

            val subtotalItem = produk.hargaProduk * produk.qty
            tvSubtotalItem.text = formatRupiah.format(subtotalItem)
            tvQtyKeranjang.text = produk.qty.toString()

            btnTambahKeranjang.setOnClickListener {
                produk.qty++
                notifyItemChanged(holder.adapterPosition)
                onQtyChanged()
            }

            btnKurangKeranjang.setOnClickListener {
                if (produk.qty > 0) {
                    produk.qty--
                    onQtyChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = listProdukAktif.size

    fun updateData(newList: List<ModelProduk>) {
        this.listProdukAktif = newList
        notifyDataSetChanged()
    }
}