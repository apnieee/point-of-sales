package com.apni.pos.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrinterAdapter(
    private val deviceList: List<BluetoothDevice>,
    private val onClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<PrinterAdapter.PrinterViewHolder>() {

    class PrinterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(android.R.id.text1)
        val tvAddress: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PrinterViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: PrinterViewHolder, position: Int) {
        val device = deviceList[position]
        holder.tvName.text = device.name ?: "Unknown Device"
        holder.tvAddress.text = device.address

        holder.itemView.setOnClickListener {
            onClick(device)
        }
    }

    override fun getItemCount(): Int = deviceList.size
}