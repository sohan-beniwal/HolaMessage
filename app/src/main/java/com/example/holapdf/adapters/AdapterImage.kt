package com.example.holapdf.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.holapdf.ImageViewActivity
import com.example.holapdf.R
import com.example.holapdf.models.ModelImage


class AdapterImage (
    private val context :Context,
    private val imageArrayList:ArrayList<ModelImage>
): RecyclerView.Adapter<AdapterImage.HolderImage>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImage {
        val view = LayoutInflater.from(context).inflate(R.layout.row_image,parent,false)

        return HolderImage(view)
    }

    override fun onBindViewHolder(holder: HolderImage, position: Int) {
        val modelImage = imageArrayList[position]

        val imageUri = modelImage.imageUri

        Glide.with(context).load(imageUri).placeholder(R.drawable.baseline_image_24).into(holder.imageIv)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ImageViewActivity::class.java)
            intent.putExtra("imageuri","$imageUri")
            context.startActivity(intent)
        }

        holder.checkBox.setOnCheckedChangeListener{view,isChecked->
            modelImage.checked = isChecked
        }
    }

    override fun getItemCount(): Int {
        return imageArrayList.size
    }

    inner class HolderImage(itemView: View) : ViewHolder(itemView){


        var imageIv = itemView.findViewById<ImageView>(R.id.imageIv)
        var checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
    }


}