package com.surftracker

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.OutputStream
import java.util.UUID

class Esp32BluetoothController {
    private var socket: BluetoothSocket? = null
    private var output: OutputStream? = null

    @SuppressLint("MissingPermission")
    fun connectToPairedEsp32(context: Context): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        val device = findPairedEsp32(adapter) ?: return false

        socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
        socket?.connect()
        output = socket?.outputStream
        return output != null
    }

    @SuppressLint("MissingPermission")
    private fun findPairedEsp32(adapter: BluetoothAdapter): BluetoothDevice? {
        val paired = adapter.bondedDevices
        return paired.firstOrNull { device ->
            val n = (device.name ?: "").lowercase()
            n.contains("esp32") || n.contains("elegoo")
        } ?: paired.firstOrNull()
    }

    fun sendMovementCommand(movement: String) {
        val cmd = when (movement) {
            "IZQUIERDA" -> "L\n"
            "DERECHA" -> "R\n"
            else -> "C\n"
        }
        output?.write(cmd.toByteArray())
    }

    fun close() {
        output?.close()
        socket?.close()
    }

    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
