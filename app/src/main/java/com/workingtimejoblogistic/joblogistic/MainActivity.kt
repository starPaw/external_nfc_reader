package com.workingtimejoblogistic.joblogistic

import com.workingtimejoblogistic.joblogistic.viewModel.MainViewModel
import com.workingtimejoblogistic.joblogistic.viewModelFactory.MainViewModelFactory
import com.workingtimejoblogistic.joblogistic.viewModel.Rpository

import android.view.View
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.ActivityManager
import android.app.PendingIntent
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.SharedPreferences

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.acs.smartcard.Reader
import com.acs.smartcard.ReaderException

class MyObserver {
    private val listeners = mutableListOf<(Int) -> Unit>()

    fun addListener(listener: (Int) -> Unit) {
        listeners.add(listener)
    }

    fun notifyListeners(number: Int) {
        for (listener in listeners) {
            listener(number)
        }
    }
}
interface DataCallback {
    fun writeCodeToNFC(number: Int)
}
public lateinit var mReader: Reader

class MainActivity : AppCompatActivity(), DataCallback {
    private val ACTION_USB_PERMISSION = "com.workingtimejoblogistic.joblogistic.USB_PERMISSION"
    private lateinit var mManager: UsbManager
//    private lateinit var mReader: Reader
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private val observer = MyObserver()
    private lateinit var mPermissionIntent: PendingIntent
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainViewModel
    companion object {
        var callback: DataCallback? = null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callback = this

        observer.addListener { number ->
            // Вызываем функцию первой активности и передаем число
            writeCodeToNFC(number)
        }
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        editText = findViewById<EditText>(R.id.edit_text)
        sendButton = findViewById<Button>(R.id.send_button)

        mManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(usbReceiver, filter)

        sharedPreferences = getSharedPreferences("USB_DEVICES", Context.MODE_PRIVATE)

        mPermissionIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE
        )
        adminLogo()
        numButtons()
        delButton()
        sendButton()

        checkConnectedDevices()
    }


    private fun adminLogo() {
        val imageView: ImageView = findViewById(R.id.Logo)
        imageView.setOnClickListener {
            intent = Intent(this, AdminActivity::class.java)
//            intent.putExtra("callbackId", callback)
            startActivity(intent)
        }
    }

    private fun numButtons() {
        //Listen touch num button
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
        //listen touch del
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

    private fun checkAuth(card: Int) {
        if (card.toString().length != 8) {
            val msg = "Номер карты должен состоять из 8 символов"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }
        val repository = Rpository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getWorkerByCard(card)

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.count() > 0) {
                val intent = Intent(this, StartStopActivity::class.java)
                intent.putExtra("cardCode", card)
                startActivity(intent)
            } else {
                val msg = "Данный номер карты не зарегистрирован"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendButton() {
        sendButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.length == 8) {
                checkAuth(text.toInt())
            } else {
                val msg = "Номер карты должен состоять из 8 символов"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
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
                    val sharedPreferences =
                        getSharedPreferences("USB_DEVICES", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean(device?.deviceName, true)
                    editor.apply()
                    if (device != null) {
                        handleDevice(device)
                    }
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
            mReader.transmit(
                slotNum, adpu, adpu.size, response,
                response.size
            )
            return response.copyOfRange(0, 16)
        } catch (e: ReaderException) {
            e.printStackTrace()
        }

        return ByteArray(0x00)
    }

    override fun writeCodeToNFC(cardId: Int){
        fun writeCode(sector: Byte, cardCode: ByteArray): ByteArray {
            return byteArrayOf(0xFF.toByte(), 0xD6.toByte(), 0x00, sector, 0x10) + cardCode
        }
        fun hexStringToByteArray(hexString: String): ByteArray {
            val result = ByteArray(hexString.length / 2)
            for ((j, i) in (hexString.indices step 2).withIndex()) {
                val byte = hexString.substring(i, i + 2).toInt(16).toByte()
                result[j] = byte
            }
            return result
        }
        val cardCode = hexStringToByteArray(
            Integer.toHexString(cardId).padStart(32, '0'))
        transmit(1, nfcAuth(0x04))
        transmit(1, writeCode(0x04, cardCode))
    }
    private fun nfcAuth(sector: Byte): ByteArray {
        return byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, sector, 0x60, 0x00)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun readCard(slotNum: Int) {
        // check if in MainActivity
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val foregroundActivity = activityManager.getRunningTasks(1)?.get(0)?.topActivity?.className
        if (foregroundActivity != "com.workingtimejoblogistic.joblogistic.MainActivity") {
            return
        }

        mReader.power(slotNum, Reader.CARD_WARM_RESET)

        try {
            mReader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)
        } catch (e: ReaderException) {
            e.printStackTrace()
        }

        fun readCode(sector: Byte): ByteArray {
            return byteArrayOf(0xFF.toByte(), 0xB0.toByte(), 0x00, sector, 0x10)
        }

        val Get_PICC =
            byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x02.toByte(), 0x00.toByte())
//        val Get_UID = byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00)
        val auth = nfcAuth(0x04)
        transmit(slotNum, auth)
        val readCode = readCode(0x04)
        val code = transmit(slotNum, readCode)
            .joinToString("") { "%02X".format(it) }.toInt(16)
        Toast.makeText(this, code.toString(), Toast.LENGTH_LONG).show()
//        checkAuth(code)
    }
}