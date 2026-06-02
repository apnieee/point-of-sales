package com.apni.pos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.databinding.ItemProdukBinding
import com.apni.pos.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class DetailProdukAdapter(
    private var listProduk: List<ModelProduk>,
    private val onItemClick: (ModelProduk) -> Unit,
    private val onStatusClick: (ModelProduk) -> Unit
) : RecyclerView.Adapter<DetailProdukAdapter.ProdukViewHolder>() {

    private val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    inner class ProdukViewHolder(val binding: ItemProdukBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val binding = ItemProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdukViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = listProduk[position]

        holder.binding.apply {
            tvNamaProduk.text = produk.namaProduk
            tvKategori.text = produk.idKategori
            tvStok.text = "Stok: ${produk.qty}"
            tvCabang.text = produk.cabang

            tvHargaProduk.text = formatRupiah.format(produk.hargaProduk)

            chipStatus.text = produk.statusProduk
            chipStatus.isChecked = (produk.statusProduk == "Aktif")

            root.setOnClickListener { onItemClick(produk) }
            chipStatus.setOnClickListener { onStatusClick(produk) }
        }
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<ModelProduk>) {
        this.listProduk = newList
        notifyDataSetChanged()
    }
}