package com.example.mydroidcam.server

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.StringBuilder
import java.net.Socket
import java.time.LocalDateTime
import kotlin.concurrent.thread

class ClientSocketHandler {
    companion object {
        val TAG = "ClientSocketHandler"
    }
    private val clientSocket: Socket;
    private val responseThread:Thread

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(sourceClientSocket:Socket) {
        this.clientSocket = sourceClientSocket;
        this.responseThread = thread( start = false) {
            this.respond()
        }
    }

    public fun respondAsync() {
        this.responseThread.run()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun respond() {
        val inputStream = this.clientSocket.getInputStream()
        val outputStream = this.clientSocket.getOutputStream()
        var requestReceived = false;

        var readBuffer = ByteArray(2048);

        while(inputStream.available()>0 &&  !requestReceived) {
            val requestLine = inputStream.bufferedReader().readLine()
            Log.i(ClientSocketHandler.TAG, requestLine)
            if(processRequestLine(requestLine)) {
                requestReceived = true;}
        }
        val sb:StringBuilder = StringBuilder()
        val sbHeader = StringBuilder()

        sb.appendLine(
            "<html>"+
                    "<head><title>Test</title></head>" +
                    "<body>"+
                    "<div>Hello from Joel's phone!</div>"+
                    "<div>"+ LocalDateTime.now().toString() +"</div>"+
                    "</body>"+
                    "</html>")
        sb.appendLine()
        val responseString = sb.toString()
        val responseBytes = responseString.toByteArray(Charsets.UTF_8)
        val responseSize = responseBytes.size

        sbHeader.appendLine("HTTP/1.1 200 OK");
        sbHeader.appendLine("Content-Type: text/html");
        sbHeader.append("Content-Length: ")
        sbHeader.appendLine(responseSize)
        sbHeader.appendLine()

        val responseHeaderString = sbHeader.toString()
        val responseHeaderBytes = responseHeaderString.toByteArray(Charsets.UTF_8)

        outputStream.write(responseHeaderBytes)
        outputStream.write(responseBytes)
        outputStream.flush()
        outputStream.close()

    }

    fun processRequestLine(requestLine:String): Boolean {
        if(requestLine == "") {
            return true;
        }
        return false;
    }

}