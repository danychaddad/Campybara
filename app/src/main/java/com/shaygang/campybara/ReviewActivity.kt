package com.shaygang.campybara

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shaygang.campybara.databinding.ActivityReviewBinding
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class ReviewActivity : AppCompatActivity() {
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var binding : ActivityReviewBinding
    private lateinit var campsiteId : String
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        firebaseDatabase = FirebaseDatabase.getInstance()
        supportActionBar?.title = "Review Campsite"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        val extras = intent.extras
        campsiteId = extras!!.getString("campsiteId").toString()
        findViewById<Button>(R.id.addReviewBtn).setOnClickListener{
            val intent = Intent(this, WriteReviewActivity::class.java)
            intent.putExtra("campsiteId", campsiteId)
            startActivity(intent)
        }
        val reviewHelper = ReviewHelper(campsiteId)
        reviewList = reviewHelper.populateReviewList {
            val avgRating = reviewHelper.calculateAvg()
            findViewById<TextView>(R.id.reviewsRatingScore).text = avgRating.toString()
            findViewById<RatingBar>(R.id.avgRatingBar).rating = avgRating
            findViewById<TextView>(R.id.avgRatingText).text = "Based on ${reviewHelper.getReviewCount()} reviews"
            populateRatingBars(reviewHelper.getScoreCounts())
        }
        getReviews()

    }


    private fun getReviews() {
        val recyclerView: RecyclerView = findViewById(R.id.reviewsRecyclerView)
        val adapter = ReviewAdapter(reviewList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun populateRatingBars(scoreCounts: Array<Int>) {
        var progressBars = arrayOf(
            R.id.reviewsProgress1,
            R.id.reviewsProgress2,
            R.id.reviewsProgress3,
            R.id.reviewsProgress4,
            R.id.reviewsProgress5
        )
        var percentages = arrayOf(
            R.id.reviewsRatingPrct1,
            R.id.reviewsRatingPrct2,
            R.id.reviewsRatingPrct3,
            R.id.reviewsRatingPrct4,
            R.id.reviewsRatingPrct5
        )
        var total = scoreCounts.sum()
        for (i in 0..4) {
            var percentage = scoreCounts[i] * 100.0f/ total
            if (percentage.isNaN()) continue;
            findViewById<ProgressBar>(progressBars[i]).progress = percentage.roundToInt()
            findViewById<TextView>(percentages[i]).text = "${percentage.roundToInt()}%"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}