package com.workingtimejoblogistic.joblogistic

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button

import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acs.smartcard.Reader
import com.acs.smartcard.ReaderException
import com.workingtimejoblogistic.joblogistic.model.Worker
import com.workingtimejoblogistic.joblogistic.viewModel.MainViewModel
import com.workingtimejoblogistic.joblogistic.viewModelFactory.MainViewModelFactory
import com.workingtimejoblogistic.joblogistic.viewModel.Rpository


class AdminActivity : AppCompatActivity() {
    private var userCode: Int = 0


    private lateinit var tableLayout: TableLayout
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val buttonBack = findViewById<Button>(R.id.buttonBackMainMEnu)
        buttonBack.setOnClickListener {
            finish()
        }

        val buttonWriteCode = findViewById<Button>(R.id.buttonWriteCode)
        buttonWriteCode.setOnClickListener {
            if (userCode != 0) {
                writeCodeToNFC(userCode)
            }

        }

        tableLayout = findViewById<TableLayout>(R.id.userTable)
        getWorkers()

    }


    private fun onRowSelected(rowIndex: Int) {

        // Сброс выделения для всех строк
        for (i in 0 until tableLayout.childCount) {
            val rowView = tableLayout.getChildAt(i) as LinearLayout
            if (i % 2 == 0) {
                rowView.setBackgroundResource(R.drawable.table_row_background)
            } else {
                rowView.setBackgroundResource(R.drawable.table_row_odd_background)
            }
        }

        // Установка выделения для выбранной строки
        val selectedRowView = tableLayout.getChildAt(rowIndex) as LinearLayout
        selectedRowView.setBackgroundColor(Color.GRAY)

        val cell = (selectedRowView.getChildAt(0) as ViewGroup).getChildAt(1) as TextView
        val cellText = cell.text.toString()
        userCode = cellText.toInt()
        Toast.makeText(this, userCode.toString(), Toast.LENGTH_SHORT).show()
    }

    fun insertTableUser(data: Array<Worker>) {


        for ((index, row) in data.withIndex()) {
            val tableRow = TableRow(this)

            if (index % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.table_row_background)
            } else {
                tableRow.setBackgroundResource(R.drawable.table_row_odd_background)
            }

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER

            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            tableRow.addView(linearLayout, layoutParams)
            val row_data = listOf(row.Name, row.card_id.toString())

            for (cellData in row_data) {
                val cell = TextView(this)
                cell.text = cellData

                val cellLayoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                cellLayoutParams.setMargins(50, 8, 50, 8)
                cell.layoutParams = cellLayoutParams

                linearLayout.addView(cell)
            }

            tableLayout.addView(tableRow)
        }

        for (i in 0 until tableLayout.childCount) {
            val rowView = tableLayout.getChildAt(i) as LinearLayout
            rowView.setOnClickListener {
                // Вызов метода при выборе строки
                onRowSelected(i)
            }
        }
    }

    private fun getWorkers() {
        val repository = Rpository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getWorker()

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.count() > 0) {
                insertTableUser(response)
            } else {
                val msg = "Нет пользователей"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        })
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

    private fun writeCodeToNFC(cardId: Int) {
        mReader.power(0, Reader.CARD_WARM_RESET)

        try {
            mReader.setProtocol(0, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)
        } catch (e: ReaderException) {
            e.printStackTrace()
        }

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
            Integer.toHexString(cardId).padStart(32, '0')
        )
        val auth = nfcAuth(0x04)
        transmit(0, auth)
        val codeCard = writeCode(0x04, cardCode)
        transmit(0, codeCard)
    }

    private fun nfcAuth(sector: Byte): ByteArray {
        return byteArrayOf(0xFF.toByte(), 0x88.toByte(), 0x00, sector, 0x60, 0x00)
    }
}

