package com.workingtimejoblogistic.joblogistic

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.acs.smartcard.Reader
import com.acs.smartcard.ReaderException


class MainActivity : AppCompatActivity() {
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    private lateinit var mManager: UsbManager
    private lateinit var mReader: Reader
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var mPermissionIntent: PendingIntent
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById<EditText>(R.id.edit_text)
        sendButton = findViewById<Button>(R.id.send_button)

        sharedPreferences = getSharedPreferences("USB_DEVICES", Context.MODE_PRIVATE)
        mManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(usbReceiver, filter)

        mPermissionIntent = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE
        )

        numButtons()
        delButton()
        sendButton()

        checkConnectedDevices()
    }

    private fun numButtons() {
        // слушатель нажатий на кнопки цифр и стирания
        val numberClickListener = View.OnClickListener { view ->
            val digit = (view as Button).text
            if (editText.text.length < 8) {
                editText.append(digit)
            }
            if (editText.text.length == 8) {
                sendButton.visibility = View.VISIBLE
            }
        }

        findViewById<Button>(R.id.button_0).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_1).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_2).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_3).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_4).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_5).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_6).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_7).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_8).setOnClickListener(numberClickListener)
        findViewById<Button>(R.id.button_9).setOnClickListener(numberClickListener)
    }

    private fun delButton() {
        // слушатель нажатия на кнопку стирания
        findViewById<Button>(R.id.button_clear).setOnClickListener {
            val text = editText.text
            if (text.isNotEmpty()) {
                editText.setText(text.subSequence(0, text.length - 1))
            }
            if (editText.text.length < 8) {
//                sendButton.visibility = View.GONE
            }
        }
    }

    private fun sendButton() {
        // слушатель нажатия на кнопку отправки
        sendButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.length == 8) {
                // выполнить API-запрос с текстом из поля ввода
                // ...
            }
            val intent = Intent(this, StartStopActivity::class.java)

            // Если нужно передать дополнительные данные, можно использовать методы putExtra()
            intent.putExtra("key", "value")

            // Запускаем активность с помощью Intent
            startActivity(intent)
        }
    }

    private fun checkConnectedDevices() {
        for (device in mManager.deviceList.values) {
            if (Reader(mManager).isSupported(device)) {
                if (sharedPreferences.getBoolean(device.deviceName, false)
                    && mManager.hasPermission(device)
                ) {
                    handleDevice(device)
                } else {
                    mManager.requestPermission(device, mPermissionIntent)
                }
            }
        }
    }

    private fun handleDevice(device: UsbDevice) {
        mReader = Reader(mManager)
        mReader.open(device)
        mReader.setOnStateChangeListener { slotNum, prevState, currState ->
            if (currState and Reader.CARD_PRESENT != 0) {
                if (slotNum != 1) {
                    runOnUiThread { readCard(slotNum) }
                }
            }
        }

    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == ACTION_USB_PERMISSION) {


                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                if (granted) {
                    // Сохранение информации об устройстве
                    val sharedPreferences =
                        getSharedPreferences("USB_DEVICES", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean(device?.deviceName, true)
                    editor.apply()
                    // Разрешение получено, можно использовать USB-устройство
                    if (device != null) {
                        handleDevice(device)
                    }
                } else {
                    // Разрешение не получено, обработайте соответствующим образом
                }
            }

            if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                for (device in mManager.deviceList.values) {
                    if (mReader.isSupported(device)) {
                        mManager.requestPermission(device, mPermissionIntent)
                    }
                }
            }
        }
    }

    fun transmit(slotNum: Int, adpu: ByteArray): ByteArray {
        val response = ByteArray(300)
        try {
            var resp_len = mReader.transmit(
                slotNum, adpu, adpu.size, response,
                response.size
            )
            return response.copyOfRange(0, 16)
        } catch (e: ReaderException) {
            e.printStackTrace()
        }

        return ByteArray(0x00)
    }

    // Initialize response text view
    @SuppressLint("SuspiciousIndentation")
    private fun readCard(slotNum: Int) {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val foregroundActivity = activityManager.getRunningTasks(1)?.get(0)?.topActivity?.className
        if (foregroundActivity != "com.workingtimejoblogistic.joblogistic.MainActivity") {
            return
        }
        editText.append("1")
        mReader.power(slotNum, Reader.CARD_WARM_RESET)

        // Создание команды чтения данных с карты
        try {
            mReader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)
        } catch (e: ReaderException) {
            e.printStackTrace()
        }

        val Get_PICC =
            byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x02.toByte(), 0x00.toByte())
        val Get_UID = byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00)



        Integer.toHexString(1695609641);

        val b = byteArrayOf(1998.toByte())
        val auth = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x00, 0x60, 0x00)
        val auth2 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x04, 0x60, 0x00)
        val auth3 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x08, 0x60, 0x00)
        val auth4 = byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, 0x0A, 0x60, 0x00)
        transmit(slotNum, auth2)
//        val aaa22 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x05, 0x10 , 0x39 , 0x55, 0x4B , 0xB2.toByte() , 0xEB.toByte() , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00, 0x00)
//        transmit(slotNum, aaa22)
        val byte16Read0 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x00, 0x10)
        val byte16Read1 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x01, 0x10)
        val byte16Read2 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x02, 0x10)
        val byte16Read3 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x03, 0x10)
        transmit(slotNum, auth)
        val byte16Read4 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x04, 0x10)
        val byte16Read5 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x05, 0x10)
        val byte16Read6 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x06, 0x10)
        val byte16Read7 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x07, 0x10)
        val byte16Read8 = byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, 0x08, 0x10)

//        val aaa11 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x04, 0x10, 0x07, 0xCE.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 , 0x00 , 0x00, 0x00)
//        transmit(slotNum, aaa11)39554BB2EB

//        val aaa33 = byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, 0x06, 0x10 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00, 0x00)
//        transmit(slotNum, aaa33)

//        val aaa = transmit(slotNum, byte16Read)
//        val aaa2 = transmit(slotNum, byte16Read2)
//        val aaa3 = transmit(slotNum, byte16Read3)
        val atqbHexString = transmit(slotNum, byte16Read0).joinToString("") { "%02X".format(it) }

    }


}