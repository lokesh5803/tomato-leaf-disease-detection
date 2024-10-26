package com.example.tomatoleafdetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tomatoleafdetection.databinding.CameraUseBinding
import com.example.tomatoleafdetection.viewmodel.CameraViewModel

class CameraUse : AppCompatActivity() {

    private lateinit var binding: CameraUseBinding
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = CameraUseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
        } else {
            startCamera()  // Start the camera if permission is already granted
        }

        // Capture button
        binding.btnCapture.setOnClickListener {
            capturePhoto()
        }
    }

    private fun startCamera() {
        viewModel.startCamera(this, binding.viewFinder)
    }

    private fun capturePhoto() {
        viewModel.capturePhoto(this, binding.viewFinder) { savedUri ->
            // Pass the captured image URI back to DiseaseClassifierActivity
            val intent = Intent().apply {
                putExtra("imageUri", savedUri.toString())
            }
            setResult(RESULT_OK, intent)
            finish()  // Finish CameraUse activity and return to DiseaseClassifierActivity
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera() // Start the camera if permission is granted
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }
}
