package com.shaygang.campybara

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.view.removeItemAt
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setCustomView(R.layout.actionbar_title)

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
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
        menu?.add("Sign Out")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        val addAdminItem = menu.findItem(R.id.)
        return super.onPrepareOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Add Admin") {
            val addAdminDialog = AddAdminDialog(this)
            addAdminDialog.show()
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