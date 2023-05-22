//package com.workingtimejoblogistic.joblogistic.utils
//
//import android.annotation.SuppressLint
//import android.app.ActivityManager
//import android.content.Context
//import com.acs.smartcard.Reader
//import com.acs.smartcard.ReaderException
//
//fun transmit(slotNum: Int, adpu: ByteArray): ByteArray {
//    val response = ByteArray(300)
//    try {
//        var resp_len = mReader.transmit(
//            slotNum, adpu, adpu.size, response,
//            response.size
//        )
//        return response.copyOfRange(0, 16)
//    } catch (e: ReaderException) {
//        e.printStackTrace()
//    }
//
//    return ByteArray(0x00)
//}
//
//// Initialize response text view
//@SuppressLint("SuspiciousIndentation")
//private fun readCard(slotNum: Int) {
//    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//    val foregroundActivity = activityManager.getRunningTasks(1)?.get(0)?.topActivity?.className
//    if (foregroundActivity != "com.workingtimejoblogistic.joblogistic.MainActivity") {
//        return
//    }
//    editText.append("1")
//    mReader.power(slotNum, Reader.CARD_WARM_RESET)
//
//    // Создание команды чтения данных с карты
//    try {
//        mReader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)
//    } catch (e: ReaderException) {
//        e.printStackTrace()
//    }
//
//    val Get_PICC =
//        byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x02.toByte(), 0x00.toByte())
//    val Get_UID = byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00)
//
//
//
//    Integer.toHexString(1695609641);
//
//    val b = byteArrayOf(1998.toByte())
//    val auth = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x00, 0x60, 0x00)
//    val auth2 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x04, 0x60, 0x00)
//    val auth3 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x08, 0x60, 0x00)
//    val auth4 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x0A, 0x60, 0x00)
//    transmit(slotNum, auth2)
////        val aaa22 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x05, 0x10 , 0x39 , 0x55, 0x4B , 0xB2.toByte() , 0xEB.toByte() , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00, 0x00)
////        transmit(slotNum, aaa22)
//    val byte16Read0 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x00, 0x10)
//    val byte16Read1 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x01, 0x10)
//    val byte16Read2 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x02, 0x10)
//    val byte16Read3 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x03, 0x10)
//    transmit(slotNum, auth)
//    val byte16Read4 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x04, 0x10)
//    val byte16Read5 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x05, 0x10)
//    val byte16Read6 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x06, 0x10)
//    val byte16Read7 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x07, 0x10)
//    val byte16Read8 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x08, 0x10)
//
////        val aaa11 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x04, 0x10, 0x07, 0xCE.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 , 0x00 , 0x00, 0x00)
////        transmit(slotNum, aaa11)39554BB2EB
//
////        val aaa33 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x06, 0x10 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00, 0x00)
////        transmit(slotNum, aaa33)
//
////        val aaa = transmit(slotNum, byte16Read)
////        val aaa2 = transmit(slotNum, byte16Read2)
////        val aaa3 = transmit(slotNum, byte16Read3)
//    val atqbHexString = transmit(slotNum, byte16Read0).joinToString("") { "%02X".format(it) }
//
//}
