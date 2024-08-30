package com.example.holapdf

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.holapdf.adapters.AdapterPdf
import com.example.holapdf.models.ModelImage
import com.example.holapdf.models.ModelPdf
import java.io.File

class PdfListFragment : Fragment() {

    private lateinit var mContext : Context
    private lateinit var pdfArrayList : ArrayList<ModelPdf>
    private lateinit var adapterPdf : AdapterPdf
    private lateinit var pdfRv : RecyclerView
    companion object{
        const val TAG = "PDF_LIST_TAG"
    }
    override fun onAttach(context: Context){
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfRv = view.findViewById(R.id.pdfRv)
        loadPdfDocuments()
    }

    private fun loadPdfDocuments() {
        Log.d(TAG,"loadPdfDocument")

        pdfArrayList= ArrayList()
        adapterPdf= AdapterPdf(mContext,pdfArrayList)
        pdfRv.adapter=adapterPdf

        val folder = File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER)

        if(folder.exists()){
            Log.d(TAG,"Folder existS")
            val files = folder.listFiles()
            if(files != null){
                Log.d(TAG,"folder have files")
                for (file in files){
                    Log.d(TAG,"${file.name}")

                    val uri = Uri.fromFile(file)

                    val modelPdf = ModelPdf(file,uri)

                    pdfArrayList.add(modelPdf)
                    adapterPdf.notifyItemInserted(pdfArrayList.size)
                }
            }
            else{
                Log.d(TAG,"Folder exists but not have files")
            }
        }
        else{
            Log.d(TAG,"Folder doesn't exists")
        }
    }
}