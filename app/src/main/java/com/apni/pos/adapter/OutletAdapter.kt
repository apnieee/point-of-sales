package com.apni.pos.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R
import com.apni.pos.model.ModelOutlet
import com.apni.pos.outlet.DetailOutletActivity

class OutletAdapter(private var listOutlet: List<ModelOutlet>) :
    RecyclerView.Adapter<OutletAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaOutlet)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamatOutlet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outlet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outlet = listOutlet[position]

        holder.tvNama.text = outlet.namaOutlet
        holder.tvAlamat.text = outlet.alamatOutlet

        holder.itemView.setOnClickListener { view ->
            val intent = Intent(view.context, DetailOutletActivity::class.java)
            intent.putExtra("OUTLET", outlet)
            view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listOutlet.size

    fun updateData(newList: List<ModelOutlet>) {
        this.listOutlet = newList
        notifyDataSetChanged()
    }
}