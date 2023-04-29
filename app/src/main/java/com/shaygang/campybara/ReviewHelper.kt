package com.shaygang.campybara

import android.util.Log
import com.google.firebase.database.*

class ReviewHelper (campsiteId : String) {
    private var reviewList : ArrayList<Review> = arrayListOf()
    private var firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reviewScoreCount = arrayOf(0,0,0,0,0)
    private val dbRef : DatabaseReference
    init {
        dbRef = firebaseDatabase.getReference("campsites/$campsiteId/reviews")
    }

    fun populateReviewList(callback : () -> Unit) : ArrayList<Review> {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reviewList.clear()
                reviewScoreCount = arrayOf(0, 0, 0, 0, 0)
                for (snapshotChild in snapshot.children) {
                    val text = snapshotChild.child("text").value.toString()
                    val authorUid = snapshotChild.child("userId").value.toString()
                    var rating = snapshotChild.child("rating").getValue(Float::class.java)!!
                    addToScoreCount(rating)
                    reviewList.add(Review(text, authorUid, rating))
                }
                callback()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return reviewList
    }

    private fun addToScoreCount(rating: Float) {
        val closestRating = when (rating) {
            in 0f..1.5f -> 1
            in 1.5f..2.5f -> 2
            in 2.5f..3.5f -> 3
            in 3.5f..4.5f -> 4
            else -> 5
        }

        // Increment the index that corresponds to the closest star rating
        reviewScoreCount[closestRating - 1]++
    }

    fun getReviewList() : ArrayList<Review> {
        return reviewList
    }
    fun getReviewCount() : Int {
        return reviewList.size
    }
    fun calculateAvg() : Float {
        var totalScore = 0f
        var totalReviews = 0
        for (i in 1..5) {
            totalScore += i * reviewScoreCount[i - 1]
            totalReviews += reviewScoreCount[i-1]
        }
        Log.d("scoreCount", reviewScoreCount.toList().toString() + " " + totalScore.toString())
        return if (totalReviews > 0) {
            totalScore / totalReviews.toFloat()
        } else {
            0f
        }
    }
}