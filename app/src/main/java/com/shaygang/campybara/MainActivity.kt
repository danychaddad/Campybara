package com.shaygang.campybara

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shaygang.campybara.Activity.ApproveOwnersActivity
import com.shaygang.campybara.Activity.CreateCampsiteActivity
import com.shaygang.campybara.Activity.SignInActivity
import com.shaygang.campybara.Adapter.ViewPagerAdapter
import com.shaygang.campybara.Dialog.AddAdminDialog
import com.shaygang.campybara.Dialog.ApplyOwnerDialog
import com.shaygang.campybara.Fragment.MapsFragment
import com.shaygang.campybara.Fragment.ProfileFragment
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

var firstName: String? = null
var lastName: String? = null
var email: String? = null
var phoneNb: String? = null
var dateOfBirth: String? = null
var profileImageUrl: String? = null
var age: Int? = null
var isAdmin: Boolean? = null
var isOwner: Boolean? = null

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var user : FirebaseUser

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setCustomView(R.layout.actionbar_title)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(HomeFragment())
        adapter.addFragment(ChatFragment())
        adapter.addFragment(SearchFragment())
        adapter.addFragment(ReservationsFragment())
        adapter.addFragment(ProfileFragment())

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> viewPager.currentItem = 0
                R.id.chatFragment -> viewPager.currentItem = 1
                R.id.searchFragment -> viewPager.currentItem = 2
                R.id.reservationsFragment -> viewPager.currentItem = 3
                R.id.profileFragment -> viewPager.currentItem = 4
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu.getItem(position).isChecked = true
            }
        })

        user = FirebaseAuth.getInstance().currentUser!!
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
        if (firstName == null || lastName == null || email == null || phoneNb == null || dateOfBirth == null || profileImageUrl == null) {
            databaseRef.get().addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    if (res.result.exists()) {
                        val dataSnapshot = res.result
                        firstName = dataSnapshot.child("firstName").value.toString()
                        lastName = dataSnapshot.child("lastName").value.toString()
                        email = dataSnapshot.child("email").value.toString()
                        phoneNb = dataSnapshot.child("phoneNb").value.toString()
                        dateOfBirth = dataSnapshot.child("dateOfBirth").value.toString()
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                        age = calculateAge(dateOfBirth!!)
                        isAdmin = dataSnapshot.child("admin").value as Boolean?
                        isOwner = dataSnapshot.child("owner").value as Boolean?

                    }
                }
            }
        }


    }

    private fun checkGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapsFragment.LOCATION_REQUEST_CODE
            )

        } else {
            // Check if GPS is enabled
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS is not enabled, show a dialog to ask the user to enable it
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Enable GPS")
                    .setMessage("Please enable GPS.")
                    .setPositiveButton("OK") { _, _ ->
                        // Open the GPS settings screen
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()

            } else {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        checkGPS()

        user = FirebaseAuth.getInstance().currentUser!!
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
        if (firstName == null || lastName == null || email == null || phoneNb == null || dateOfBirth == null || profileImageUrl == null) {
            databaseRef.get().addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    if (res.result.exists()) {
                        val dataSnapshot = res.result
                        firstName = dataSnapshot.child("firstName").value.toString()
                        lastName = dataSnapshot.child("lastName").value.toString()
                        email = dataSnapshot.child("email").value.toString()
                        phoneNb = dataSnapshot.child("phoneNb").value.toString()
                        dateOfBirth = dataSnapshot.child("dateOfBirth").value.toString()
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                        age = calculateAge(dateOfBirth!!)
                        isAdmin = dataSnapshot.child("admin").value as Boolean?
                        isOwner = dataSnapshot.child("owner").value as Boolean?
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Add Admin")
        menu?.add("Become An Owner")
        menu?.add("Approve Owners")
        menu?.add("Sign Out")
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val addAdminItem = menu?.getItem(0)
        val applyForOwner = menu?.getItem(1)
        val approveOwners = menu?.getItem(2)

        if (addAdminItem != null && approveOwners != null && isAdmin == false) {
            addAdminItem.isVisible = false
            approveOwners.isVisible = false
        }

        if (applyForOwner != null) {
            if (isAdmin == true || isOwner == true) {
                applyForOwner.isVisible = false
            } else if (age != null && age!! < 18) {
                applyForOwner.isVisible = false
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Add Admin") {
            val addAdminDialog = AddAdminDialog(this)
            addAdminDialog.show()
        }
        if (item.title == "Become An Owner") {
            val applyOwnerDialog = ApplyOwnerDialog(this)
            applyOwnerDialog.show()
        }
        if (item.title == "Approve Owners") {
            val intent = Intent(this@MainActivity, ApproveOwnersActivity::class.java)
            startActivity(intent)
        }
        if (item.title == "Sign Out") {
            MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure?")
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Yes") { _, _ ->
                    firstName = null
                    lastName = null
                    email = null
                    phoneNb = null
                    dateOfBirth = null
                    profileImageUrl = null
                    age = null
                    isAdmin = null
                    isOwner = null

                    firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .show()
        }

        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAge(dateString: String): Int {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val birthDate = LocalDate.parse(dateString, formatter)
        val currentDate = LocalDate.now()
        val res = Period.between(birthDate, currentDate).years
        return res
    }
}