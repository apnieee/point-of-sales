package com.apni.pos.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.model.ModelPelanggan
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class PelangganAdapter(
    private var listPelanggan: List<ModelPelanggan>,
    private val onPilih: (ModelPelanggan) -> Unit,
    private val onRiwayat: (ModelPelanggan) -> Unit,
    private val onEdit: (ModelPelanggan) -> Unit,
    private val onHapus: (ModelPelanggan) -> Unit
) : RecyclerView.Adapter<PelangganAdapter.ViewHolder>() {

    private val avatarColors = listOf(
        "#1565C0", "#6A1B9A", "#E65100", "#00695C", "#AD1457", "#4527A0"
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvNama: TextView = view.findViewById(R.id.tvItemNamaPelanggan)
        val tvHp: TextView = view.findViewById(R.id.tvItemHpPelanggan)
        val tvTotal: TextView = view.findViewById(R.id.tvItemTotalTransaksi)
        val btnPilih: MaterialButton = view.findViewById(R.id.btnPilihMember)
        val btnRiwayat: MaterialButton = view.findViewById(R.id.btnRiwayatMember)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnEditMember)
        val btnHapus: MaterialButton = view.findViewById(R.id.btnHapusMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        val inisial = pelanggan.namaPelanggan
            .split(" ")
            .take(2)
            .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        val warna = avatarColors[pelanggan.namaPelanggan.length % avatarColors.size]
        holder.tvAvatar.text = inisial
        (holder.tvAvatar.background as? GradientDrawable)?.setColor(Color.parseColor(warna))

        holder.tvNama.text = pelanggan.namaPelanggan
        holder.tvHp.text = pelanggan.nomorHp
        holder.tvTotal.text = "Total transaksi: ${formatRupiah(pelanggan.totalTransaksi)}"

        holder.btnPilih.setOnClickListener { onPilih(pelanggan) }
        holder.btnRiwayat.setOnClickListener { onRiwayat(pelanggan) }
        holder.btnEdit.setOnClickListener { onEdit(pelanggan) }
        holder.btnHapus.setOnClickListener { onHapus(pelanggan) }
    }

    override fun getItemCount() = listPelanggan.size

    fun updateData(newList: List<ModelPelanggan>) {
        listPelanggan = newList
        notifyDataSetChanged()
    }

    private fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount).replace(",00", "")
    }
}