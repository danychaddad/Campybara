package com.shaygang.campybara

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class WriteReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)
        supportActionBar?.title = "Add a review"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        findViewById<Button>(R.id.submitReviewBtn).setOnClickListener {
            checkValues()
        }
    }

    private fun checkValues() {
        val reviewET = findViewById<TextInputEditText>(R.id.reviewInputText)
        val text = reviewET.text.toString()
        if (text.isEmpty()) {
            reviewET.error = "Field cannot be empty"
            return
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val campsiteId = intent.extras?.getString("campsiteId")!!
        val rating = findViewById<RatingBar>(R.id.newReviewRatingBar).rating
        addReviewToDatabase(text,uid,campsiteId,rating)
    }

    private fun addReviewToDatabase(text: String, uid: String, campsiteId: String, rating : Float) {
        val ref = FirebaseDatabase.getInstance().getReference("campsites/$campsiteId/reviews")
        val review = Review(text, uid, rating)
        ref.push().setValue(review).addOnSuccessListener {
            Toast.makeText(this,"Successfully added review!", Toast.LENGTH_SHORT).show()
            finish()
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