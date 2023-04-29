package com.shaygang.campybara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shaygang.campybara.databinding.ActivityReviewBinding
import org.w3c.dom.Text

class ReviewActivity : AppCompatActivity() {
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var binding : ActivityReviewBinding
    private lateinit var campsiteId : String;
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
            findViewById<TextView>(R.id.reviewsRatingScore).text = reviewHelper.calculateAvg().toString()
        }
        val recyclerView: RecyclerView = findViewById(R.id.reviewsRecyclerView)
        val adapter = ReviewAdapter(reviewList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
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