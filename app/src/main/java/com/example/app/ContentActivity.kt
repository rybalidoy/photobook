package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        //Change StatusBar Color
        val window : Window = this@ContentActivity.window
        window.statusBarColor = ContextCompat.getColor(this@ContentActivity,R.color.BLACK)

        var intent = intent
        val contentDate = intent.getStringExtra("iDate")
        val contentDescription = intent.getStringExtra("iDescription")
        val contentImage = intent.getByteArrayExtra("iImageView")
        val contentId = intent.getIntExtra("iItem_id",0)

        a_date.text = contentDate
        a_description.text = contentDescription

        //Set image resource
        Glide.with(this)
            .asDrawable()
            .load(contentImage)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(405,300)
            .into(a_image)

        //a_image.setImageResource(contentImage)

        //ContentView -> EditView
        val editBtn = findViewById<Button>(R.id.editBtn)
        editBtn.setOnClickListener {
            intent = Intent(this,EditActivity::class.java)
            intent.putExtra("iDate",contentDate)
            intent.putExtra("iDescription",contentDescription)
            intent.putExtra("iImageView",contentImage)
            intent.putExtra("iItem_id",contentId)
            startActivity(intent)
        }

    }
}