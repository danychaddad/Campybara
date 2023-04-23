package com.shaygang.campybara

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.shaygang.campybara.databinding.ActivityCampsiteDetailsBinding

class CampsiteDetailsActivity : AppCompatActivity() {

    private lateinit var campsiteName : String
    private lateinit var campsiteImageUrl : String
    private lateinit var binding: ActivityCampsiteDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campsite_details)
        binding = ActivityCampsiteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras;
        campsiteName = extras!!.getString("campsiteName")!!
        campsiteImageUrl = extras!!.getString("imageUrl")!!
        supportActionBar?.hide()
        val titleTextView = binding.campsiteName
        Glide.with(this).load(campsiteImageUrl).placeholder(R.drawable.capy_loading_image).into(binding.campsiteImage)
        titleTextView.text = campsiteName
        binding.ownerLayout.setOnClickListener{

        }
    }


}