package com.shaygang.campybara

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaygang.campybara.User.Companion.loadUserFromUid
import com.shaygang.campybara.databinding.ActivityCampsiteDetailsBinding
import java.util.Locale

class CampsiteDetailsActivity : AppCompatActivity() {

    private lateinit var campsiteName : String
    private lateinit var campsiteImageUrl : String
    private lateinit var binding: ActivityCampsiteDetailsBinding
    private lateinit var campsiteOwnerUid : String
    private lateinit var campsiteOwner : User
    private lateinit var campsiteId : String
    private lateinit var campsiteLocation: ArrayList<Double>
    private lateinit var fragment: CampsiteDetailsMapsFragment
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campsite_details)
        binding = ActivityCampsiteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        campsiteName = extras!!.getString("campsiteName")!!
        campsiteImageUrl = extras.getString("imageUrl")!!
        campsiteOwnerUid = extras.getString("ownerUid")!!
        campsiteId = extras.getString("campsiteId").toString()
        campsiteLocation = extras.get("campsiteLocation") as ArrayList<Double>
        supportActionBar?.hide()

        val titleTextView = binding.campsiteName
        Glide.with(this).load(campsiteImageUrl).placeholder(R.drawable.capy_loading_image).into(binding.campsiteImage)
        titleTextView.text = campsiteName
        loadUserFromUid(campsiteOwnerUid) { user ->
            if (user != null) {
                // TODO: Add a shimmer effect loading
                campsiteOwner = user
                val ownerFullName = user.firstName + " " + user.lastName
                binding.ownerName.text = ownerFullName
                binding.ownerEmail.text = user.email
                Glide.with(this).load(user.profileImageUrl).placeholder(R.drawable.capy_loading_image).into(binding.ownerProfilePic)
            } else {
                // Handle the error
            }
        }
        binding.ratingLayout.setOnClickListener{
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("campsiteId",campsiteId)
            intent.putExtra("campsiteName",campsiteName)
            startActivity(intent)
        }

        binding.openGoogleMaps.setOnClickListener {
            val intentUri = Uri.parse("geo:${campsiteLocation[0]},${campsiteLocation[1]}")
            val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
            startActivity(mapIntent)
        }

        if (campsiteOwnerUid == FirebaseAuth.getInstance().currentUser?.uid) {
            binding.chatOwner.isVisible = false
        }
        binding.chatOwner.setOnClickListener {
            fetchUser(campsiteOwnerUid)
        }

        CAMPSITE_LOCATION = campsiteLocation
        fragment = CampsiteDetailsMapsFragment()
        supportFragmentManager.beginTransaction().replace(R.id.campsiteMapLayout, fragment).commit()

        geocoder = Geocoder(this, Locale.getDefault())
        val address =  geocoder.getFromLocation(campsiteLocation[0], campsiteLocation[1], 1)
        binding.campsiteAddress.text = address?.get(0)?.getAddressLine(0).toString()
    }

    private fun fetchUser(uid: String) {
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("users").orderByChild("uid").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val firstName = child.child("firstName").value.toString()
                    val lastName = child.child("lastName").value.toString()
                    val userId = child.child("uid").value.toString()
                    val profileUrl = child.child("profileImageUrl").value.toString()

                    val userName = "$firstName $lastName"
                    val intent = Intent(this@CampsiteDetailsActivity, ChatActivity::class.java)
                    intent.putExtra(ChatFragment.USERPIC_KEY, profileUrl)
                    intent.putExtra(ChatFragment.USERNAME_KEY, userName)
                    intent.putExtra(ChatFragment.USERID_KEY, userId)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {
        lateinit var CAMPSITE_LOCATION: ArrayList<Double>
    }
}