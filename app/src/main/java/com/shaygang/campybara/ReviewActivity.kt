package com.shaygang.campybara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shaygang.campybara.databinding.ActivityReviewBinding

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
        reviewList = arrayListOf()
        var dbRef = firebaseDatabase.getReference("campsites/$campsiteId/reviews")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reviewList.clear()
                for (snapshotChild in snapshot.children){
                    val text = snapshotChild.child("text").value.toString()
                    val authorUid = snapshotChild.child("userId").value.toString()
                    var rating = snapshotChild.child("rating").getValue(Float::class.java)!!
                    reviewList.add(Review(text, authorUid, rating))
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
//        reviewList = listOf(
//            Review("Beautiful location with great views of the mountains. The facilities were clean and well-maintained.", "John Doe", 4.5f),
//            Review("We had a great time camping here. The staff were friendly and helpful, and the campsite was spacious and comfortable.", "Jane Smith", 4.0f),
//            Review("The campsite was too crowded and noisy for our liking, and the bathrooms were not very clean. We would not stay here again.", "Mark Johnson", 2.0f),
//            Review("This is a wonderful spot for camping. The site was secluded and peaceful, and the lake nearby was perfect for swimming and fishing.", "Samantha Lee", 5.0f),
//            Review("We had a great time camping here with our family. The campfire pit and picnic table were a nice touch, and the staff were very welcoming.", "Robert Davis", 4.5f),
//            Review("The campsite was in a beautiful location, but the facilities were outdated and not very clean. We would recommend bringing your own portable toilet.", "Jennifer Thompson", 2.5f),
//            Review("This campsite was the perfect spot for a weekend getaway. The hiking trails nearby were scenic and challenging, and the campsite itself was cozy and comfortable.", "David Brown", 5.0f),
//            Review("We had a bit of trouble finding the campsite, but once we arrived, we were impressed by the stunning views and quiet surroundings. The staff were helpful and accommodating.", "Emily Wilson", 4.0f),
//            Review("The campsite was overcrowded and noisy, and the bathrooms were not well-maintained. We would not recommend staying here if you are looking for a peaceful retreat.", "Ryan Taylor", 2.0f),
//            Review("This was the best camping experience we have had in a long time. The campsite was well-equipped and the staff were friendly and helpful. We would definitely come back here again.", "Jessica Garcia", 5.0f)
//        )
        // Set up the RecyclerView and its adapter
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