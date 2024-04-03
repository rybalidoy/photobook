package com.example.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.grid.view.*
import kotlinx.android.synthetic.main.row.view.*
import java.io.ByteArrayInputStream

class GalleryAdapter(val arrayList: ArrayList<Model>,val context: Context):
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindItems(model : Model) {

            val byteImage = model.image
            val stream : ByteArrayInputStream = ByteArrayInputStream(byteImage)
            val new_image : Bitmap = BitmapFactory.decodeStream(stream)
            itemView.imageGallery.setImageBitmap(new_image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grid,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])

        holder.itemView.setOnClickListener {
            val model = arrayList.get(position)
            val gDate : String = model.date
            val gDescription : String = model.description
            val byteImage = model.image
            val stream = ByteArrayInputStream(byteImage)
            val new_image : Bitmap = BitmapFactory.decodeStream(stream)
            val gImageView : ByteArray = model.image

            val intent = Intent(context,ContentActivity::class.java)
            intent.putExtra("iDate", gDate)
            intent.putExtra("iDescription",gDescription)
            intent.putExtra("iImageView",gImageView)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}