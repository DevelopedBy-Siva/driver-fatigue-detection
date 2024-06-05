package com.driver.drowsiness.detection.services

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.ByteArrayOutputStream
import java.net.URI

class VideoWebSocketClient(uri: URI) : WebSocketClient(uri) {

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

    fun sendVideoFrame(frameData: ByteArray) {
        if (isOpen) {
            // Convert frameData to Base64 encoded string
            val base64Data =
                android.util.Base64.encodeToString(frameData, android.util.Base64.DEFAULT)
            // Send the Base64 encoded string
            send(base64Data)
        }
    }

}
