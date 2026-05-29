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
    private val onItemClick: (ModelProduk) -> Unit
) : RecyclerView.Adapter<DetailProdukAdapter.ProdukViewHolder>() {

    inner class ProdukViewHolder(private val binding: ItemProdukBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(produk: ModelProduk) {
            binding.tvNamaProduk.text = produk.namaProduk
            binding.tvKategoriProduk.text = produk.idKategori

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            formatRupiah.maximumFractionDigits = 0

            binding.tvHargaProduk.text = formatRupiah.format(produk.hargaProduk)

            binding.root.setOnClickListener {
                onItemClick(produk)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val binding = ItemProdukBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdukViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        holder.bind(listProduk[position])
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<ModelProduk>) {
        this.listProduk = newList
        notifyDataSetChanged()
    }
}