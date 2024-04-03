package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.add_btn
import kotlinx.android.synthetic.main.activity_main.create_btn
import kotlinx.android.synthetic.main.activity_main.gallery_btn

class GalleryActivity : AppCompatActivity() {

    //Floating Button Resources
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim)}
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim)}
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim)}
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim)}

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        //Change StatusBar Color
        val window : Window = this@GalleryActivity.window
        window.statusBarColor = ContextCompat.getColor(this@GalleryActivity,R.color.BLACK)

        //Floating Button
        add_btn.setOnClickListener{
            onAddButtonClicked()
        }
        create_btn.setOnClickListener{
            Toast.makeText(this,"Create Button Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }
        gallery_btn.setOnClickListener{
            Toast.makeText(this,"Gallery View Button Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,GalleryActivity::class.java)
            startActivity(intent)
        }

        //Program Here
        setUpListofDataIntoRecyclerView()

    }
    fun setUpListofDataIntoRecyclerView() {
        if(getItemList().size > 0){
            val source = GridLayoutManager(this,2)
            gridRecyclerView.layoutManager = source
            //mainRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
            val itemAdapter = GalleryAdapter(getItemList(),this)
            gridRecyclerView.adapter = itemAdapter
        }
    }

    fun getItemList(): ArrayList<Model> {
        val databaseHandler : DatabaseHandler = DatabaseHandler(this)
        val list : ArrayList<Model> = databaseHandler.viewDatabase()
        return list
    }


    //Floating Button Methods
    private fun onAddButtonClicked(){

        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked

    }
    private fun setVisibility(clicked: Boolean){
        if (!clicked){
            create_btn.visibility = View.VISIBLE
            gallery_btn.visibility = View.VISIBLE
        } else {
            create_btn.visibility = View.GONE
            gallery_btn.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked: Boolean){
        if(!clicked){
            create_btn.startAnimation(fromBottom)
            gallery_btn.startAnimation(fromBottom)
            add_btn.startAnimation(rotateOpen)
        } else {
            create_btn.startAnimation(toBottom)
            gallery_btn.startAnimation(toBottom)
            add_btn.startAnimation(rotateClose)
        }
    }
    private fun setClickable(clicked: Boolean){
        if(!clicked){
            create_btn.isClickable = true
            gallery_btn.isClickable = true
        } else {
            create_btn.isClickable = false
            gallery_btn.isClickable = false
        }
    }

}