package com.example.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*
import java.io.ByteArrayInputStream

class MyAdapter(val arrayList: ArrayList<Model>,val context: Context):
    RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindItems(model : Model) {

            itemView.itemDate.text = model.date
            itemView.itemDescription.text = model.description
            val byteImage = model.image
            val stream : ByteArrayInputStream = ByteArrayInputStream(byteImage)
            val new_image : Bitmap = BitmapFactory.decodeStream(stream)
            itemView.sampleImage.setImageBitmap(new_image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row,parent,false)
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
            val item_id = model.id

            val intent = Intent(context,ContentActivity::class.java)
            intent.putExtra("iDate", gDate)
            intent.putExtra("iDescription",gDescription)
            intent.putExtra("iImageView",gImageView)
            intent.putExtra("iItem_id",item_id)
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}