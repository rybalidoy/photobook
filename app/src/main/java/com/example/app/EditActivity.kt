package com.example.app

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.current_date
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File

private const val FILE_NAME = "photo.jpg"
private const val REQUEST_CODE = 42
private const val IMAGE_REQUEST_CODE = 100

class EditActivity : AppCompatActivity() {

    private lateinit var byteArray: ByteArray
    private lateinit var photoFile : File
    private lateinit var takenImage : Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //Change StatusBar Color
        val window : Window = this@EditActivity.window
        window.statusBarColor = ContextCompat.getColor(this@EditActivity,R.color.BLACK)

        var intent = intent
        val contentDate = intent.getStringExtra("iDate")
        val contentDescription = intent.getStringExtra("iDescription")
        val contentImage = intent.getByteArrayExtra("iImageView")
        val contentId = intent.getIntExtra("iItem_id",0)

        update_current_date.text = contentDate
        update_description.setText(contentDescription)

        //Set image resource
        Glide.with(this)
            .asDrawable()
            .load(contentImage)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(405,300)
            .into(update_image)
        update_image.visibility = View.VISIBLE
        //a_image.setImageResource(contentImage)
        byteArray = contentImage!!

        //Need to refrseh contentactivity
        //Update Record
        up_photo_photo.setOnClickListener{
            showDialog()
        }
        update_btn.setOnClickListener{
            val id = contentId
            updateRecord(id)
        }

        //Delete Record
        delete_btn.setOnClickListener{
            val id = contentId
            deleteRecord(id)
        }

        //Create Activity Methods
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
                .into(update_image)
            update_image.visibility = View.VISIBLE
            //edit_image.setImageBitmap(takenImage)

        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val imageUri = data?.data
            Glide.with(this)
                .load(imageUri)
                .into(update_image)
            update_image.visibility = View.VISIBLE
            val image = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG,70,stream)
            byteArray = stream.toByteArray()
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


    private fun updateRecord(id : Int){

        val date = update_current_date.text.toString()
        val description = update_description.text.toString()
        val image = byteArray


        val databaseHandler : DatabaseHandler = DatabaseHandler(this)
        if(!date.isEmpty() && !description.isEmpty()){
            val status =
                databaseHandler.updateRecord(Model(id,date,description,byteArray))
        }
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    private fun deleteRecord(id : Int){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete the item.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteRecord(Model(id, "", "",byteArray))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()
            }
            dialogInterface.dismiss() // Dialog will be dismissed
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }


}