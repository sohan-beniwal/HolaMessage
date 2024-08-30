package com.example.holapdf

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import android.provider.Settings
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.holapdf.adapters.AdapterImage
import com.example.holapdf.models.ModelImage
import java.util.concurrent.Executors
import kotlin.Exception

class ImageListFragment : Fragment() {

    companion object {
        private const val TAG = "IMAGE_LIST_TAG"

        private const val STORAGE_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }
    private lateinit var cameraPermissions : Array<String>
    private lateinit var storagePermissions: Array<String>
    private lateinit var mContext : Context
    private lateinit var addImageTab : FloatingActionButton
    private var imageUri : Uri? = null
    private lateinit var allImageArrayList : ArrayList<ModelImage>
    private lateinit var adapterImage: AdapterImage
    private lateinit var imagesRv : RecyclerView
    private lateinit var progressDialog : ProgressDialog
    override fun onAttach(context: Context){
        mContext = context
        super.onAttach(context)
    }

    private fun saveImageToAppLevelDirectory(imageUriToBeSaved:Uri){
        Log.d(TAG,"sAVE IMAGE TO APP LEVEL DIRECTORY: ${imageUriToBeSaved}")
       try {
           val bitmap: Bitmap
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
               bitmap = ImageDecoder.decodeBitmap(
                   ImageDecoder.createSource(
                       mContext.contentResolver,
                       imageUriToBeSaved
                   )
               )
           } else {
               bitmap =
                   MediaStore.Images.Media.getBitmap(mContext.contentResolver, imageUriToBeSaved)
           }
           val directory = File(mContext.getExternalFilesDir(null), Constants.IMAGES_Folder)
           directory.mkdirs()
           val timestamp = System.currentTimeMillis()
           val fileName = "$timestamp.jpeg"

           val file =
               File(mContext.getExternalFilesDir(null), "${Constants.IMAGES_Folder}/$fileName")

           try {
               val fos = FileOutputStream(file)
               bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
               fos.flush()
               fos.close()
               Log.d(TAG,"SAVE IMAGE TO APP LEVEL DIRECTORY")
               toast("Image Saved")
           } catch (e: Exception) {
                Log.d(TAG,"SAVE IMAGE TO APPLEVEL DIRECTORY FAILED ${e.message}")
           }
       }catch (e: Exception) {
           Log.d(TAG,"SAVE IMAGE TO APPLEVEL DIRECTORY FAILED ${e.message}")
       }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_image,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if(itemId==R.id.images_item_delete){
            val builder = AlertDialog.Builder(mContext)

            builder.setTitle("Delete Image(s)").setMessage("Are you sure you want to delete All/Selected images?")
                .setPositiveButton("DELETE ALL"){
                    dialog,which->
                    deleteImages(true)
                }.setNeutralButton("DELETE SELECTED"){
                    dialog,which->
                    deleteImages(false)
                }.setNegativeButton("CANCEL"){
                    dialog,which->
                    dialog.dismiss()
                }.show()
        }
        else if(itemId == R.id.images_item_pdf){
            val builder = AlertDialog.Builder(mContext)

            builder.setTitle("Convert to PDF").setMessage("Convert All/Selected images to PDF")
                .setPositiveButton("CONVERT ALL"){
                        dialog,which->
                    convertImagesToPdf(true)
                }.setNeutralButton("CONVERT SELECTED"){
                        dialog,which->
                    convertImagesToPdf(false)
                }.setNegativeButton("CANCEL"){
                        dialog,which->
                    dialog.dismiss()
                }.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun convertImagesToPdf(convertAll: Boolean) {
        Log.d(TAG,"Converting images to pdf ")

        progressDialog.setMessage("Converting....")
        progressDialog.show()

        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute{

            Log.d(TAG,"Background work")
            var imagesToPdfList = ArrayList<ModelImage>()
            if(convertAll){
                imagesToPdfList = allImageArrayList
            }
            else{
                for( i in allImageArrayList.indices){
                    if(allImageArrayList[i].checked){
                        imagesToPdfList.add(allImageArrayList[i])
                    }
                }
            }

            try{
                val root = File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER)
                root.mkdirs()

                val timestamp = System.currentTimeMillis()
                val filename = "PDF_${timestamp}.pdf"

                val file = File(root,filename)
                val fileOutputStream = FileOutputStream(file)
                val pdfDocument = PdfDocument()
                for(i in imagesToPdfList.indices){

                    val imageToPdfUri = imagesToPdfList[i].imageUri
                    Log.d(TAG,"$imageToPdfUri")
                    try {
                        var bitmap :Bitmap
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.contentResolver, imageToPdfUri))
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, imageToPdfUri)
                        }
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

                        val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, i + 1).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val paint = Paint()
                        paint.color = Color.WHITE
                        val canvas = page.canvas
                        canvas.drawPaint(paint)
                        canvas.drawBitmap(bitmap,0f,0f,null)
                        pdfDocument.finishPage(page)
                        bitmap.recycle()
                    }
                    catch (e: Exception) {
                        Log.d(TAG, "Convert images to pdf exception ${e.message}")
                    }
                }
                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
            }catch(e:Exception){
                Log.d(TAG,"Convert images to pdf exception ${e.message}")
            }

            handler.post{
                progressDialog.dismiss()
                Log.d(TAG,"Converted")
                toast("Converted")

                allImageArrayList.forEach { modelImage ->
                    modelImage.checked = false
                }
                imagesRv.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun deleteImages(deleteAll : Boolean){
        var imageToDeleteList = ArrayList<ModelImage>()

        if(deleteAll){
            imageToDeleteList = allImageArrayList
        }
        else{
            for(modelImage in allImageArrayList){
                if(modelImage.checked){
                    imageToDeleteList.add(modelImage)
                    Log.d(TAG,"${imageToDeleteList[0].imageUri}")
                }
            }
        }

        for(modelImage in imageToDeleteList){
            try{
                var path = modelImage.imageUri.path
                var file = File(path)
                Log.d(TAG,"$imageUri")
                if(file.exists()){
                    val isDeleted = file.delete()
                    Log.d(TAG,"$isDeleted")
                }
            }catch (e:Exception){
                Log.d(TAG,"${e.message}")
            }
        }
        toast("Deleted")
        loadImages()
        imagesRv.adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        imagesRv = view.findViewById(R.id.imageRv)

        addImageTab = view.findViewById(R.id.imageAddTab)

        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadImages()
        addImageTab.setOnClickListener{
            showInputImageDialog()
        }
    }

    private fun showInputImageDialog(){
        val popupMenu = PopupMenu(mContext,addImageTab)

        popupMenu.menu.add(Menu.NONE,1,1,"CAMERA")
        popupMenu.menu.add(Menu.NONE,2,2,"GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->
            val itemId = menuItem.itemId

            if(itemId==1){
                if(checkCameraPermission()){
                    pickImageCamera()
                }
                else{
                    requestCameraPermission()
                }
            }
            else{
                Log.d(TAG, "Requesting storage permission")
                if(checkStoragePermission()){
                    pickImageGallery()
                }
                else{
                    requestStoragePermission()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun loadImages(){
        Log.d(TAG,"loadImages")

        allImageArrayList = ArrayList()

        adapterImage = AdapterImage(mContext,allImageArrayList)

        imagesRv.adapter = adapterImage

        val folder = File(mContext.getExternalFilesDir(null),Constants.IMAGES_Folder)

        if(folder.exists()){
            Log.d(TAG,"Folder exista")
            val files = folder.listFiles()
            if(files != null){
                Log.d(TAG,"folder have files")
                for (file in files){
                    Log.d(TAG,"${file.name}")

                    val imageUri = Uri.fromFile(file)

                    val modeImage = ModelImage(imageUri,false)

                    allImageArrayList.add(modeImage)
                    adapterImage.notifyItemInserted(allImageArrayList.size)
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

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "ImagePicking")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (checkStoragePermission()) {
                    val data = result.data
                    if (data != null) {
                        val clipData = data.clipData
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(i).uri
                                saveImageToAppLevelDirectory(imageUri)
                                val modeImage = ModelImage(imageUri, false)
                                allImageArrayList.add(modeImage)
                            }
                            adapterImage.notifyItemRangeInserted(allImageArrayList.size - clipData.itemCount, clipData.itemCount)
                        } else {
                            Log.e(TAG, "ClipData is null")
                        }
                    }
                } else {
                    Log.e(TAG, "Storage permission not granted")
                }
            } else {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        val clipData = data.clipData
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(i).uri
                                saveImageToAppLevelDirectory(imageUri)
                                val modeImage = ModelImage(imageUri, false)
                                allImageArrayList.add(modeImage)
                            }
                            adapterImage.notifyItemRangeInserted(allImageArrayList.size - clipData.itemCount, clipData.itemCount)
                        } else {
                            val imageUri = data.data
                            if (imageUri != null) {
                                saveImageToAppLevelDirectory(imageUri)
                                val modeImage = ModelImage(imageUri, false)
                                allImageArrayList.add(modeImage)
                                adapterImage.notifyItemInserted(allImageArrayList.size)
                            } else {
                                Log.e(TAG, "Intent data is null")
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Cancelled")
                    toast("Cancelled")
                }
            }
        }
    private var imageUris: MutableList<Uri> = mutableListOf()
    private var isCapturing = false

    private fun pickImageCamera() {
        val intent = Intent(mContext, CustomCameraActivity::class.java)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageUris = data?.getParcelableArrayListExtra<Uri>("imageUris")
            if (imageUris != null) {
                // Handle the captured image URIs
                for (imageUri in imageUris) {
                    // Process the imageUri as needed
                    saveImageToAppLevelDirectory(imageUri)
                    val modeImage = ModelImage(imageUri, false)
                    allImageArrayList.add(modeImage)
                    adapterImage.notifyItemInserted(allImageArrayList.size)
                }
            }
        } else {
            Log.d(TAG, "Cancelled")
            toast("Cancelled")
        }
    }


    private fun checkStoragePermission(): Boolean {
        Log.d(TAG, "checkstoragepermission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // For versions below Android 11, assume permission is granted
        }
    }

    private fun requestStoragePermission() {
        Log.d(TAG, "requeststoragepermission")
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + mContext.packageName)
            galleryActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching permission intent: ${e.message}")
        }
    }

    private fun checkCameraPermission():Boolean{
        Log.d(TAG,"Checkcamerapermission")
        val cameraResult = ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val storageResult = checkStoragePermission()
        val temp = cameraResult&&storageResult
        Log.d(TAG,"${storageResult}")
        return temp;
    }

    private fun requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE)
    }

    private fun toast(string : String){
        Toast.makeText(mContext,string,Toast.LENGTH_SHORT).show()
    }
}