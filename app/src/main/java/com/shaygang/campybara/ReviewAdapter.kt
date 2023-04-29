package com.shaygang.campybara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val reviewList: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewList[position]
        holder.username.text = review.userId
        holder.text.text = review.text
        holder.ratingBar.rating = review.rating
    }

    override fun getItemCount() = reviewList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.ratingUsername)
        val text: TextView = itemView.findViewById(R.id.reviewItemText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.reviewStarRating)
    }
}
