package com.shaygang.campybara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CampsiteAdapter (private val campsiteList: ArrayList<Campsites>) : RecyclerView.Adapter<CampsiteAdapter.CampsiteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampsiteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return CampsiteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return campsiteList.size
    }

    override fun onBindViewHolder(holder: CampsiteViewHolder, position: Int) {
        val currentItem = campsiteList[position]
        Glide.with(holder.itemView).load(currentItem.campsiteImageURL).into(holder.campsiteImage)
        holder.campsiteName.text = currentItem.campsiteName
    }

    class CampsiteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val campsiteImage : ImageView = itemView.findViewById(R.id.campsiteImage)
        val campsiteName : TextView = itemView.findViewById(R.id.campsiteName)
    }
}