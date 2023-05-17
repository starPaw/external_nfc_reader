package com.workingtimejoblogistic.joblogistic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class StartStopActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var looper: Looper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_stop)

        // Initialize the output directory
        outputDirectory = getOutputDirectory()

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        captureButton.setOnClickListener {
            takePhoto()
        }
        looper = Looper.myLooper() ?: Looper.getMainLooper()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), resources.getString(R.string.app_name)
        ).apply { mkdirs() }
        return if (mediaDir.exists())
            mediaDir else filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (ex: Exception) {
                // Handle camera binding exception
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
//        val imageCapture = imageCapture ?: return
//
//        val photoFile = File(
//            outputDirectory,
//            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
//        )
//
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // Photo capture success callback
//                    val savedUri = Uri.fromFile(photoFile)

                    tableWrite2()
                    val message = "View will be closed in 5 seconds..."
                    val textView = findViewById<TextView>(R.id.textView)
                    textView.text = message
                    textView.visibility = View.VISIBLE
                    previewView.visibility = View.GONE
                    captureButton.visibility = View.GONE
                    handler = Handler()
                    runnable = Runnable {
//                        textView.visibility = View.GONE
//                        previewView.visibility =  View.VISIBLE
//                        captureButton.visibility = View.VISIBLE
                        goToMainScreen()
                    }
                    handler.postDelayed(runnable, 5000)
                }

//                override fun onError(exception: ImageCaptureException) {
//                    // Photo capture error callback
//                    val errorMsg = "Photo capture failed: ${exception.message}"
//                    Toast.makeText(this@StartStopActivity, errorMsg, Toast.LENGTH_SHORT).show()
//                }
//            }
//        )
//    }

    private fun tableWrite2() {
// Получить ссылку на TableLayout
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

// Создать и заполнить данные для таблицы
        val data = listOf(
            listOf("          Cell 1", "Cell 2          "),
            listOf("          Cell 3", "Cell 4          "),
            listOf("Cell 5", "Cell 6")
        )

// Пройти по каждой строке данных
        for ((index, row) in data.withIndex()) {
            // Создать новую строку таблицы
            val tableRow = TableRow(this)

            // Установить фон для строки таблицы
            if (index % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.table_row_background)
            } else {
                tableRow.setBackgroundResource(R.drawable.table_row_odd_background)
            }

            // Создать LinearLayout для размещения двух ячеек
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            tableRow.addView(linearLayout, layoutParams)

            // Пройти по каждой ячейке в строке
            for (cellData in row) {
                // Создать новую ячейку
                val cell = TextView(this)
                cell.text = cellData

                // Установить параметры для ячейки
                val cellLayoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
                cellLayoutParams.setMargins(50, 8, 50 , 8)
                cell.layoutParams = cellLayoutParams

                // Добавить ячейку в LinearLayout
                linearLayout.addView(cell)
            }

            // Добавить строку в таблицу
            tableLayout.addView(tableRow)
        }
    }
    private fun tableWrite() {
        // Получить ссылку на TableLayout
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        // Создать и заполнить данные для таблицы
        val data = listOf(
            listOf("Cell 1", "Cell 2", "Cell 3"),
            listOf("Cell 4", "Cell 5", "Cell 6"),
            listOf("Cell 7", "Cell 8", "Cell 9")
        )

// Пройти по каждой строке данных
        for ((index, row) in data.withIndex()) {
            // Создать новую строку таблицы
            val tableRow = TableRow(this)

            // Установить фон для строки таблицы
            if (index % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.table_row_background)
            } else {
                tableRow.setBackgroundResource(R.drawable.table_row_odd_background)
            }

            // Пройти по каждой ячейке в строке
            for ((cellIndex, cellData) in row.withIndex()) {
                // Создать новую ячейку
                val cell = TextView(this)
                cell.text = cellData

//                // Установить отступы для ячейки
//                val padding = resources.getDimensionPixelSize(R.dimen.cell_padding)
//
//                cell.setPadding(padding, padding, padding, padding)
                // Установить отступы для первой и последней ячейки
//                val paddingStart = resources.getDimensionPixelSize(R.dimen.cell_padding_start)
//                val paddingEnd = resources.getDimensionPixelSize(R.dimen.cell_padding_end)
//                val paddingTopBottom =
//                    resources.getDimensionPixelSize(R.dimen.cell_padding_top_bottom)
//
//                if (cellIndex == 0) {
//                    cell.setPadding(paddingStart, paddingTopBottom, 0, paddingTopBottom)
//                } else if (cellIndex == row.size - 1) {
//                    cell.setPadding(0, paddingTopBottom, paddingEnd, paddingTopBottom)
//                } else {
//                    cell.setPadding(0, paddingTopBottom, 0, paddingTopBottom)
//                }

                // Установить гравитацию для ячейки
                cell.gravity = Gravity.CENTER


                // Установить параметры ячейки
                val layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                // Добавить дополнительный Space перед каждой ячейкой, кроме первой
                if (cellIndex != 0) {
                    val space = Space(this)
                    val spaceLayoutParams = TableRow.LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.cell_spacing),
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    tableRow.addView(space, spaceLayoutParams)
                }

                cell.layoutParams = layoutParams

                // Добавить ячейку в строку
                tableRow.addView(cell)
            }

            // Добавить строку в таблицу
            tableLayout.addView(tableRow)
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private fun goToMainScreen() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Очистка обработчика и удаление запланированной задачи при уничтожении активности
        handler.removeCallbacks(runnable)
    }
}
