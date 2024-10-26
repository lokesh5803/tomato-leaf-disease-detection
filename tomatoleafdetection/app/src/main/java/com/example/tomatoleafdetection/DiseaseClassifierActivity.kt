package com.example.tomatoleafdetection

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.tomatoleafdetection.databinding.ActivityDiseaseClassifierBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class DiseaseClassifierActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiseaseClassifierBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var uploadLauncher: ActivityResultLauncher<Intent>
    private lateinit var interpreter: Interpreter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityDiseaseClassifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the TFLite model
        interpreter = Interpreter(loadModelFile("model_unquant.tflite"))

        // Camera and Upload Launchers
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    binding.imageView.setImageURI(uri)
                    performPrediction(uri)
                }
            }
        }

        uploadLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                Glide.with(this).load(imageUri).into(binding.imageView)
                imageUri?.let { performPrediction(it) }
            }
        }

        binding.buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        binding.buttonUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            uploadLauncher.launch(intent)
        }
    }

    private fun loadModelFile(filename: String): ByteBuffer {
        assets.openFd(filename).use { assetFileDescriptor ->
            FileInputStream(assetFileDescriptor.fileDescriptor).channel.use { inputChannel ->
                val startOffset = assetFileDescriptor.startOffset
                val declaredLength = assetFileDescriptor.declaredLength
                return inputChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
                    .apply { order(ByteOrder.nativeOrder()) }
            }
        }
    }

    private fun performPrediction(imageUri: Uri) {
        try {
            val bitmap = loadImageFromUri(imageUri)
            val inputBuffer = preprocessImage(bitmap)
            val output = Array(1) { FloatArray(10) }
            interpreter.run(inputBuffer, output)
            val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
            binding.predictionResult.text = getDiseaseName(predictedIndex)
        } catch (e: Exception) {
            Log.e("DiseaseClassifierActivity", "Prediction failed", e)
            binding.predictionResult.text = "Prediction error"
        }
    }

    private fun loadImageFromUri(imageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source).run {
                if (width != 224 || height != 224) Bitmap.createScaledBitmap(this, 224, 224, true) else this
            }
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri).run {
                Bitmap.createScaledBitmap(this, 224, 224, true)
            }
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) - 127) / 128f)
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) - 127) / 128f)
            byteBuffer.putFloat(((pixel and 0xFF) - 127) / 128f)
        }
        return byteBuffer
    }

    private fun getDiseaseName(index: Int): String {
        return when (index) {
            0 -> "Tomato mosaic virus"
            1 -> "Target Spot"
            2 -> "Bacterial spot"
            3 -> "Tomato Yellow Leaf Curl Virus"
            4 -> "Late blight"
            5 -> "Leaf Mold"
            6 -> "Early blight"
            7 -> "Spider mites Two spotted spider mite"
            8 -> "Tomato healthy"
            9 -> "Septoria leaf spot"
            else -> "Unknown disease"
        }
    }

    override fun onDestroy() {
        interpreter.close()
        super.onDestroy()
    }
}
