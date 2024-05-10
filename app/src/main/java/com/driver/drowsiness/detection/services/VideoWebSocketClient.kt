package com.driver.drowsiness.detection.services

import android.widget.Toast
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class VideoWebSocketClient(uri: URI) : WebSocketClient(uri) {
    private var isClosed = false

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("Socket Established...")
    }

    override fun onMessage(message: String?) {
        println("Incoming...")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("Socket Closed...")
    }

    override fun onError(ex: Exception?) {
        println("Socket Error...")
        close()
    }

}
