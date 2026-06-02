package com.apni.pos.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apni.pos.R

class PrinterActivity : AppCompatActivity() {

    private lateinit var rvPrinter: RecyclerView
    private lateinit var btnCari: Button
    private lateinit var ivKembali: ImageView
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer)

        rvPrinter = findViewById(R.id.rvPrinter)
        btnCari = findViewById(R.id.btnCariPrinter)
        ivKembali = findViewById(R.id.ivKembali)

        rvPrinter.layoutManager = LinearLayoutManager(this)

        ivKembali.setOnClickListener {
            finish()
        }

        btnCari.setOnClickListener {
            checkPermissionsAndBluetooth()
        }
    }

    private fun checkPermissionsAndBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 100)
                return
            }
        }

        checkBluetooth()
    }

    private fun checkBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "HP tidak support Bluetooth", Toast.LENGTH_SHORT).show()
        } else if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Aktifkan Bluetooth dulu!", Toast.LENGTH_SHORT).show()
        } else {
            tampilkanDaftarPrinter()
        }
    }

    private fun tampilkanDaftarPrinter() {
        try {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

            if (pairedDevices.isNullOrEmpty()) {
                Toast.makeText(this, "Tidak ada perangkat Bluetooth yang ter-pairing", Toast.LENGTH_SHORT).show()
            } else {
                val deviceList = pairedDevices.toList()
                val adapter = PrinterAdapter(deviceList) { selectedDevice ->
                    Toast.makeText(this, "Memilih printer: ${selectedDevice.name}", Toast.LENGTH_SHORT).show()
                }
                rvPrinter.adapter = adapter
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Izin Bluetooth belum diberikan!", Toast.LENGTH_SHORT).show()
        }
    }
}