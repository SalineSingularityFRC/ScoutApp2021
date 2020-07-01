package com.scouting.ssss

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.util.Log
import bluetooth.Bluetooth
import bluetooth.CommunicationCallback


// If we ever want to get fancy, we can use error codes like this to handle errors with the handler
const val MESSAGE_ERR = 1

public class BluetoothClass(a: MainActivity?) {
    val activity: MainActivity? = a
    //private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val tag = "7G7 Bluetooth"
    private var setup = false
    var pendingData: String = ""
    var partialMessage = ""
    // TODO: Handler is deprecated, what's an alternative?
    private val handler = Handler()
    private val bluetooth: Bluetooth
    // TODO: How do we avoid hardcoding the MAC addr?
    private val macAddr: String = "B8:27:EB:24:CF:68" //Put the bluetooth address of you Pi server here

    init {
        this.bluetooth = Bluetooth(this.activity)
    }

    private val CCB: CommunicationCallback = object : CommunicationCallback {
        override fun onConnect(device: BluetoothDevice) {
            Log.i(
                tag,
                "Connected to device " + device.name + " at " + device.address + "!"
            )
            bluetooth.send(pendingData)
        }

        override fun onDisconnect(device: BluetoothDevice, message: String) {
            Log.i(
                tag,
                "Disconnected from device " + device.name + " at " + device.address + "!"
            )
            if (pendingData.isNotEmpty()) {
                reconnect()
            }
        }

        override fun onMessage(message: String) {
            Log.i(tag, "Got a message: '$message'")
            // TODO: This is a hack
            // if the file is too long, the pi sends the data in chunks which leads to corrupted JSON.
            // we add each message to pendingData until we find the end is a ], which *should* always mean the end of a message
            // there are probably edge cases though, so this should get fixed
//            partialMessage += message
//
//            if (message[message.length - 1] == ']') {
//                Log.i(tag, "Sending message '$partialMessage'")
//                activity?.database?.dataSent(partialMessage)
//                Log.i(tag, "Data transfer complete!")
//                partialMessage = ""
//            }
            Log.i(tag, "Sending message '$message'")
            activity?.database?.dataSent(message)
            Log.i(tag, "Data transfer complete")
        }

        override fun onError(message: String) {
            Log.e(tag, "Generic error: $message")
        }

        override fun onConnectError(device: BluetoothDevice, message: String) {
            Log.e(tag, "Caught connection error: $message")
            reconnect()
        }
    }

    init {
        bluetooth.setCommunicationCallback(CCB)
    }

    fun setup() {
        if (!setup) {
            bluetooth.onStart()
        }
        if (!bluetooth.isEnabled) {
            bluetooth.enable()
        }
        setup = true
    }

    private fun reconnect() {
        handler.postDelayed({ bluetooth.connectToAddress(macAddr) }, 3000)
    }

    fun send(data: String) {
        Log.i(tag, "Entered the send method in bluetooth")
        if (pendingData.isEmpty()) bluetooth.connectToAddress(macAddr)
        pendingData = data
    }

    fun end() {
        bluetooth.onStop()
    }
}