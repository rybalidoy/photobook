package com.example.app

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val FILE_NAME = "photo.jpg"
private const val REQUEST_CODE = 42
private const val IMAGE_REQUEST_CODE = 100

class CreateActivity : AppCompatActivity() {

    private lateinit var byteArray: ByteArray
    private lateinit var photoFile : File
    private lateinit var takenImage : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        //Get Current Date
        val date = Calendar.getInstance().time
        val sdf = SimpleDateFormat.getDateInstance()
        val formatedDate = sdf.format(date)

        current_date.setText(formatedDate)

        //Change StatusBar Color
        val window : Window = this@CreateActivity.window
        window.statusBarColor = ContextCompat.getColor(this@CreateActivity,R.color.BLACK)

        add_photo.setOnClickListener{
            //Call
            showDialog()
        }



        //Save Record
        save_btn.setOnClickListener{
            addRecord()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog)
        dialog.show()

        val galleryBtn : Button = dialog.findViewById(R.id.button_gallery)
        val cameraBtn : Button = dialog.findViewById(R.id.button2)
        dialog.setCancelable(false)

        galleryBtn.setOnClickListener{
            //For gallery Image
            pickImageGallery()
            dialog.dismiss()
        }
        cameraBtn.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(this,"com.example.app.fileprovider",photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)

            if(takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent,REQUEST_CODE)
            } else {
                Toast.makeText(
                    this,
                    "Unable to open camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

    }
    private fun setUpListofDataIntoRecyclerView() {
        if(getItemList().size > 0){
            mainRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
            val itemAdapter = MyAdapter(getItemList(),this)
            mainRecyclerView.adapter = itemAdapter
        }
    }
    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_REQUEST_CODE)
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName,".jpg",storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //val takenImage = data?.extras?.get("data") as Bitmap
            takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            val stream = ByteArrayOutputStream()
            takenImage.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            byteArray = stream.toByteArray()
            Glide.with(this)
                .load(byteArray)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(405,300)
                .into(edit_image)
            edit_image.visibility = View.VISIBLE
            //edit_image.setImageBitmap(takenImage)

        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val imageUri = data?.data
            Glide.with(this)
                .load(imageUri)
                .into(edit_image)
            edit_image.visibility = View.VISIBLE
            val image = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG,70,stream)
            byteArray = stream.toByteArray()
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    //Database Methods
    private fun addRecord() {
        val date = current_date.text.toString()
        val description = edit_text.text.toString()
        val image = byteArray

        val databaseHandler : DatabaseHandler = DatabaseHandler(this)
        if (!date.isEmpty() && !description.isEmpty()) {
            val status =
                databaseHandler.addRecord(Model(0,date,description,image))
            if (status > -1){
                Toast.makeText(applicationContext,"Record Saved", Toast.LENGTH_SHORT).show()
                edit_text.text.clear()
            }
        }
    }
    private fun getItemList(): ArrayList<Model> {
        val databaseHandler : DatabaseHandler = DatabaseHandler(this)
        val list : ArrayList<Model> = databaseHandler.viewDatabase()
        return list
    }

}