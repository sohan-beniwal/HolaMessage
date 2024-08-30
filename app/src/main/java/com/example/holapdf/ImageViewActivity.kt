package com.example.holapdf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    private lateinit var imageView : ImageView
    private var imageUri = ""

    companion object{
        const val TAG = "IMAGE_VIEW_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)


        supportActionBar?.title = "Image View"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        imageView = findViewById(R.id.imageIv)

        imageUri = intent.getStringExtra("imageuri").toString()
        Log.d(TAG,"$imageUri")


        Glide.with(this).load(imageUri).placeholder(R.drawable.baseline_image_24).into(imageView)
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return super.onSupportNavigateUp()
    }
}