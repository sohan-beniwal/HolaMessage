package com.example.holapdf

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

class CustomCameraActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var captureButton: Button
    private lateinit var doneButton: Button
    private lateinit var cancelButton: Button
    private lateinit var capturedImageView : ImageView

    private val imageUris: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera)

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)
        doneButton = findViewById(R.id.doneButton)
        cancelButton = findViewById(R.id.cancelButton)
        capturedImageView = findViewById(R.id.capturedView)
        captureButton.setOnClickListener {
            takePicture()
        }

        doneButton.setOnClickListener {
            onDone()
        }

        cancelButton.setOnClickListener {
            onCancel()
        }

        // Create an image capture use case
        imageCapture = ImageCapture.Builder().build()

        // Bind the preview to the lifecycle
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Select back camera as the default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Attach the use cases to the camera with the same lifecycle owner
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))

        // Add touch listener to the parent layout
        findViewById<View>(R.id.layoutCamera).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && capturedImageView.visibility == View.VISIBLE) {
                // Hide the preview when clicked outside
                capturedImageView.visibility = View.GONE
                previewView.visibility = View.VISIBLE
                true
            } else {
                false
            }
        }
    }

    private fun takePicture() {
        val photoFile = File(externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                imageUris.add(savedUri)

                // Show the captured image in the ImageView
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                capturedImageView.setImageBitmap(bitmap)
                capturedImageView.visibility = View.VISIBLE
                previewView.visibility = View.GONE // Hide the camera preview
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun onDone() {
        val intent = Intent()
        intent.putParcelableArrayListExtra("imageUris", ArrayList(imageUris))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onCancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        private const val TAG = "CustomCameraActivity"
    }
}

