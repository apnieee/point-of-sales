package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemKeranjangBinding
import com.apni.pos.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class AdapterKeranjang(
    private val listProdukAktif: MutableList<ModelProduk>,
    private val onQtyChanged: () -> Unit
) : RecyclerView.Adapter<AdapterKeranjang.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKeranjangBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKeranjangBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProdukAktif[position]
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }

        holder.binding.apply {
            tvNamaProdukKeranjang.text = produk.namaProduk
            tvHargaSatuanKeranjang.text = "${formatRupiah.format(produk.hargaProduk)} / item"
            tvSubtotalItem.text = formatRupiah.format(produk.hargaProduk * produk.qty)
            tvQtyKeranjang.text = produk.qty.toString()

            btnTambahKeranjang.setOnClickListener {
                produk.qty++
                notifyItemChanged(holder.adapterPosition)
                onQtyChanged()
            }

            btnKurangKeranjang.setOnClickListener {
                if (produk.qty > 1) {
                    produk.qty--
                    notifyItemChanged(holder.adapterPosition)
                    onQtyChanged()
                } else {
                    produk.qty = 0
                    val pos = holder.adapterPosition
                    listProdukAktif.removeAt(pos)
                    notifyItemRemoved(pos)
                    onQtyChanged()
                }
            }
        }
    }

    override fun getItemCount() = listProdukAktif.size

    fun updateData(newList: List<ModelProduk>) {
        listProdukAktif.clear()
        listProdukAktif.addAll(newList)
        notifyDataSetChanged()
    }
}