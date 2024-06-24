package com.example.mydroidcam.server

interface IStatusUpdateReceiver {
    fun updateStatus(ipAddress:String, clientCount:Int);
}