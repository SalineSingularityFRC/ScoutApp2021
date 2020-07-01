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

// If we ever want to get fancy, we can use error codes like this to handle errors with the handler
const val MESSAGE_ERR = 1

public class BluetoothClass(a: MainActivity) {
    val activity: MainActivity = a
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val tag = "7G7 Bluetooth"
    private var setup = false
    // TODO: Handler is deprecated, what's an alternative?
    private val handler = Handler()
    // TODO: This could probably be made non-optional with some magic
    private var bluetooth: ConnectThread? = null
    // TODO: How do we avoid hardcoding the MAC addr?
    private val macAddr: String = "B8:27:EB:24:CF:68" //Put the bluetooth address of you Pi server here

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

    init {
        while (!setup) {
            val dev = matchFromPaired()
            // TODO: Handle this better
            if (dev == null) {
                Log.e(tag, "Couldn't find target device!")
                setup = false
            } else {
                Log.i(tag, "Got device with address ${dev.address}")
                bluetooth = ConnectThread(dev)
                //bluetooth?.run()
                setup = true
            }
        }
    }

    fun send(data: String) {
        bluetooth?.run(data)
    }

    // Thread for connecting to a device
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val uuid = UUID.randomUUID()
        private val device = device

        // run the thread
        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            val mmSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {
                //device.createRfcommSocketToServiceRecord(uuid)
                Log.i(tag, "Creating RFCOMM socket")
                getStream(device)
            }

            mmSocket.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.

                try {
                    socket.connect()
                    //var stream = fallbackSocket.outputStream
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
            }
        }

        // The same as this.run(), but sends a ByteArray over the socket
        public fun run(data: String) {
            // Turn off discovery to speed connection
            bluetoothAdapter?.cancelDiscovery()

            val mmSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {
                //device.createRfcommSocketToServiceRecord(uuid)
                Log.i(tag, "Creating RFCOMM socket")
                getStream(device)
            }

            // 'use' automatically closes the session at the end of scope
            mmSocket.use { socket ->
                try {
                    socket.connect()
                    //var stream = fallbackSocket.outputStream
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (socket.outputStream == null) {
                    Log.e(tag, "Null output stream; won't write data")
                }  else {
                    // Connection succeeded; write our data
                    Log.i(tag, "Writing '$data' over the connection")
                    socket.outputStream.bufferedWriter().use {
                        it.write(data)
                    }
                }
            }
        }

        // Write data to the bluetooth socket to send data to a remote device
//        public fun write(stream: OutputStream, bytes: ByteArray) {
//            try {
//                stream.write(bytes)
//            } catch (e: IOException) {
//                Log.e(tag, "Error occurred when sending data", e)
//
//                // Send a failure message back to the activity.
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_ERR)
//                val bundle = Bundle().apply {
//                    putString(tag, "Couldn't send data to the other device")
//                }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)
//                return
//            }
//        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                //mmSocket?.close()
            } catch (e: IOException) {
                Log.e(tag, "Could not close the client socket", e)
            }
        }

        // TODO: is there a better way to do this?
        fun getStream(device: BluetoothDevice): BluetoothSocket {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            val clazz = socket.remoteDevice.javaClass
            val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
            val m = clazz.getMethod("createRfcommSocket", *paramTypes)
            return m.invoke(socket.remoteDevice, Integer.valueOf(1)) as BluetoothSocket
        }
    }
}