package com.scouting.ssss

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.util.*

// If we ever want to get fancy, we can use error codes like this to handle errors
const val MESSAGE_ERR = 1

public class BluetoothClass() {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val tag = "7G7 Bluetooth"
    private var setup = false
    private val handler = BtHandler(WeakReference(this))
    // TODO: This could probably be made non-optional with some magic
    private var bluetooth: ConnectThread? = null
    // TODO: How do we avoid hardcoding the MAC addr?
    private val macAddr: String = "B8:27:EB:E8:64:53" //Put the bluetooth address of you Pi server here

    private fun matchFromPaired(): BluetoothDevice? {
        val paired: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        paired?.forEach {
            // Match the address of each paired device against our target MAC address
            when (it.address) {
                macAddr -> return it
                else -> {}
            }
        }
        return null
    }

    // initialize the bluetooth connection
     init {
        // Hang until a connection succeeds
        // right now this just makes a blank screen which isn't ideal
        // TODO: impl a loading screen?
        while (!setup) {
            val dev = matchFromPaired()
            // TODO: Handle this better
            if (dev == null) {
                Log.i(tag, "Couldn't find target device!")
                setup = false
            } else {
                bluetooth = ConnectThread(dev)
                bluetooth?.run()
                setup = true
            }
        }
    }

    fun send(data: ByteArray) {
        bluetooth?.run(data)
    }

    // Thread for connecting to a device
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val uuid = UUID.randomUUID()
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }
        // TODO: Maybe make this OutputStream? and drop the !!
        private val mmOutStream: OutputStream = mmSocket!!.outputStream

        // run the thread
        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
            }
        }

        // The same as this.run(), but sends a ByteArray over the socket
        public fun run(data: ByteArray) {
            // Turn off discovery to speed connection
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                socket.connect()

                // Connection succeeded; write our data
                this.write(data)
            }
        }

        // Write data to the bluetooth socket to send data to a remote device
        public fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(tag, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_ERR)
                val bundle = Bundle().apply {
                    putString(tag, "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(tag, "Could not close the client socket", e)
            }
        }
    }

    private inner class BtHandler(private val outerClass: WeakReference<BluetoothClass>) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // TODO: Handle messages
        }
    }
}