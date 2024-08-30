package com.example.holapdf.adapters

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.holapdf.Methods
import com.example.holapdf.R
import com.example.holapdf.models.ModelPdf
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AdapterPdf(private val context : Context , private val pdfArrayList: ArrayList<ModelPdf>) :
    Adapter<AdapterPdf.HolderPdfView>(){
companion object{
    private const val TAG = "ADAPTER_PDF_TAG"
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfView {
        val view = LayoutInflater.from(context).inflate(R.layout.row_pdf,parent,false)
        return HolderPdfView(view)
    }

    override fun onBindViewHolder(holder: HolderPdfView, position: Int) {

        val modelPdf = pdfArrayList[position]
        val name = modelPdf.file.name
        val timestamp = modelPdf.file.lastModified()
        val formattedDate = Methods.formatTimestamp(timestamp)

        loadThumbNailFromPdf(modelPdf,holder)
        loadFileSize(modelPdf,holder)

        holder.nameTv.text = name
        holder.dateTv.text = formattedDate

    }

    private fun loadFileSize(modelPdf: ModelPdf, holder: AdapterPdf.HolderPdfView) {
        Log.d(TAG,"loadFileSize")

        val bytes : Double = modelPdf.file.length().toDouble()
        val kb = bytes/1024
        var size=""
        val mb=kb/1024

        if(mb>=1){
            size=String.format("%.2f",mb)+"MB"
        }
        else if(kb>=1){
            size = String.format("%.2f",kb)+"KB"
        }
        else{
            size=String.format("%.2f",bytes)+"bytes"
        }
        Log.d(TAG,"loadFileSize ${size}")

        holder.sizeTv.text=size
    }

    private fun loadThumbNailFromPdf(modelPdf: ModelPdf, holder: AdapterPdf.HolderPdfView) {
    Log.d(TAG,"loadThumbNailPdf")

        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute {
            var thumbnailBitmap : Bitmap? = null
            var pageCount = 0;

            try{
                val parcelFileDescriptor = ParcelFileDescriptor.open(modelPdf.file,ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                pageCount = pdfRenderer.pageCount
                if (pageCount<=0){
                    Log.d(TAG,"loadThumbNailPdf NO PAGES")
                }
                else{
                    val currentPage = pdfRenderer.openPage(0)
                    thumbnailBitmap = Bitmap.createBitmap(currentPage.width,currentPage.height,Bitmap.Config.ARGB_8888)
                    currentPage.render(thumbnailBitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }
            }catch (e:Exception){
                Log.d(TAG,"loadThumbNailPdf ${e.message}")
            }

            handler.post{
                Log.d(TAG,"loadThumbNailPdf Setting thumbnail and page count")
                Glide.with(context).load(thumbnailBitmap).fitCenter().placeholder(R.drawable.baseline_picture_as_pdf_24).into(holder.thumbnailIv)
                holder.pagesTv.text = "$pageCount pages"
            }
        }
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    inner class HolderPdfView(itemView : View) : ViewHolder(itemView){

        var thumbnailIv : ImageView = itemView.findViewById(R.id.thumbnailIv)
        var nameTv : TextView = itemView.findViewById(R.id.nameTv)
        var sizeTv : TextView = itemView.findViewById(R.id.sizeTv)
        var dateTv : TextView = itemView.findViewById(R.id.dateTv)
        var moreBtn : ImageButton = itemView.findViewById(R.id.moreBtn)
        var pagesTv : TextView = itemView.findViewById(R.id.pagesTv)
    }
}