package com.shaygang.campybara

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CampsiteAdapter(private val campsiteIdList : ArrayList<String>, val context: Context) : RecyclerView.Adapter<CampsiteAdapter.CampsiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampsiteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return CampsiteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return campsiteIdList.size
    }

    override fun onBindViewHolder(holder: CampsiteViewHolder, position: Int) {
        var currentItemId = campsiteIdList[position]
        Campsite.getCampsiteFromId(currentItemId) {
            val currentItem = it!!
            Glide.with(holder.itemView).load(currentItem.imageUrl.toString()).placeholder(R.drawable.capy_loading_image).into(holder.campsiteImage)
            holder.campsiteName.text = currentItem.name
            val reviewHelper = ReviewHelper(currentItemId)
            reviewHelper.populateReviewList {
                holder.campsiteRating.text = String.format("%.1f",reviewHelper.calculateAvg())
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, CampsiteDetailsActivity::class.java)
                intent.putExtra("campsiteName", currentItem.name)
                intent.putExtra("imageUrl", currentItem.imageUrl)
                intent.putExtra("ownerUid", currentItem.ownerUID)
                intent.putExtra("campsiteId",currentItemId)
                intent.putExtra("campsiteLocation", currentItem.location)
                context.startActivity(intent)
            }
        }
    }

    class CampsiteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val campsiteImage : ImageView = itemView.findViewById(R.id.campsiteImage)
        val campsiteName : TextView = itemView.findViewById(R.id.campsiteName)
        val campsiteRating : TextView = itemView.findViewById(R.id.itemListRatingScore)
    }
}