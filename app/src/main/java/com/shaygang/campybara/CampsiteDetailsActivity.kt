package com.shaygang.campybara

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaygang.campybara.User.Companion.loadUserFromUid
import com.shaygang.campybara.databinding.ActivityCampsiteDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class CampsiteDetailsActivity : AppCompatActivity() {

    private lateinit var campsiteName: String
    private lateinit var campsiteImageUrl: String
    private lateinit var binding: ActivityCampsiteDetailsBinding
    private lateinit var campsiteOwnerUid: String
    private lateinit var campsiteOwner: User
    private lateinit var campsiteId: String
    private lateinit var campsiteLocation: ArrayList<Double>
    private lateinit var fragment: CampsiteDetailsMapsFragment
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campsite_details)
        binding = ActivityCampsiteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        loadExtras(extras)
        supportActionBar?.title = campsiteName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val titleTextView = binding.campsiteName
        Glide.with(this).load(campsiteImageUrl).placeholder(R.drawable.capy_loading_image)
            .into(binding.campsiteImage)
        titleTextView.text = campsiteName
        loadOwner()
        loadReviews()
        binding.ratingLayout.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("campsiteId", campsiteId)
            intent.putExtra("campsiteName", campsiteName)
            startActivity(intent)
        }

        if (campsiteOwnerUid == FirebaseAuth.getInstance().currentUser?.uid || age!! < 18) {
            binding.chatOwner.isVisible = false
        }
        binding.chatOwner.setOnClickListener {
            fetchUser(campsiteOwnerUid)
        }
        loadMap()
        binding.reserveCampsiteBtn.setOnClickListener {
            reserveCampsiteDialog()
        }
    }

    private fun loadMap() {
        CAMPSITE_LOCATION = campsiteLocation
        fragment = CampsiteDetailsMapsFragment()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // perform any long-running operations here, such as loading data from a network or database
                supportFragmentManager.beginTransaction().replace(R.id.campsiteMapLayout, fragment)
                    .commit()
            }
        }
        geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(campsiteLocation[0], campsiteLocation[1], 1)
        binding.campsiteAddress.text = address?.get(0)?.getAddressLine(0).toString()
    }

    private fun reserveCampsiteDialog() {
        val dialog = Dialog(this)
        var calendarView : CalendarView
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reservation_dialog_group)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<Button>(R.id.resDialogGroupCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.resDialogGroupNext).setOnClickListener {
            dialog.setContentView(R.layout.reservation_dialog_date)
            dialog.findViewById<TextView>(R.id.resDialogDateTxt).text = "Enter Reservation Start Date"
            val selectedFromDate = Date()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val calendarDateSelect = dialog.findViewById<CalendarView>(R.id.resDialogDateSelect)
            var startDate: Date? = null
            calendarDateSelect.setOnDateChangeListener { calendarView, year, month, day ->
                selectedFromDate.time = calendar.timeInMillis
                startDate = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            }
            dialog.findViewById<Button>(R.id.resDialogDateCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.resDialogDateNext).setOnClickListener {
                dialog.setContentView(R.layout.reservation_dialog_date)
                calendarView = dialog.findViewById<CalendarView>(R.id.resDialogDateSelect)
                dialog.findViewById<TextView>(R.id.resDialogDateTxt).text = "Enter Reservation End Date"
                val minDate = Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_MONTH, 1)
                }.timeInMillis
                val maxDate = Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_MONTH, 14)
                }.timeInMillis
                calendarView.minDate = minDate
                calendarView.maxDate = maxDate
                dialog.findViewById<Button>(R.id.resDialogDateCancel).setOnClickListener {
                    dialog.dismiss()
                }
                var selectedToDate : Date = Date()
                dialog.findViewById<CalendarView>(R.id.resDialogDateSelect).setOnDateChangeListener { calendarView, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year,month,day)
                    selectedToDate.time = calendar.timeInMillis
                }
                dialog.findViewById<Button>(R.id.resDialogDateNext).setOnClickListener {
                    dialog.setContentView(R.layout.reservation_dialog_confirmation)
                    dialog.findViewById<TextView>(R.id.resDialogConfirmFromDate).text = selectedFromDate.toString()
                    dialog.findViewById<TextView>(R.id.resDialogConfirmToDate).text = selectedToDate.toString()
                    dialog.findViewById<Button>(R.id.resDialogConfirmCancel).setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.findViewById<Button>(R.id.resDialogConfirmFinish).setOnClickListener {
                        Toast.makeText(this,"Successfully sent reservation!",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun loadExtras(extras: Bundle?) {
        campsiteName = extras!!.getString("campsiteName")!!
        campsiteImageUrl = extras.getString("imageUrl")!!
        campsiteOwnerUid = extras.getString("ownerUid")!!
        campsiteId = extras.getString("campsiteId")!!
        campsiteLocation = extras.get("campsiteLocation") as ArrayList<Double>
    }

    private fun loadReviews() {
        val reviewHelper = ReviewHelper(campsiteId)
        reviewHelper.populateReviewList {
            val avg = reviewHelper.calculateAvg()
            findViewById<TextView>(R.id.ratingScore).text = avg.toString()
            findViewById<RatingBar>(R.id.ratingBar).rating = avg
            findViewById<TextView>(R.id.ratingText).text =
                "Based on ${reviewHelper.getReviewCount()} reviews"
        }
    }

    private fun loadOwner() {
        loadUserFromUid(campsiteOwnerUid) { user ->
            if (user != null) {
                // TODO: Add a shimmer effect loading
                campsiteOwner = user
                val ownerFullName = user.firstName + " " + user.lastName
                findViewById<TextView>(R.id.ownerName).text = ownerFullName
                findViewById<TextView>(R.id.ownerEmail).text = user.email
                Glide.with(this).load(user.profileImageUrl)
                    .placeholder(R.drawable.capy_loading_image).into(findViewById(R.id.ownerProfilePic))
            } else {
                // Handle the error
            }
        }
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