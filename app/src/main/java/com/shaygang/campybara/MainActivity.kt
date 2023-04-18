package com.shaygang.campybara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// mongodb
// username : application
// password : fd2mZeDzVQ4rPvNY

var firstName: String? = null
var lastName: String? = null
var email: String? = null
var phoneNb: String? = null
var dateOfBirth: String? = null
var profileImageUrl: String? = null

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)

        supportActionBar?.setDisplayShowHomeEnabled(true)

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
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Sign Out")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Sign Out") {
            MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure?")
                .setNegativeButton("No") { _, _ -> }
                .setPositiveButton("Yes") { _, _ ->
                    firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }

        return super.onOptionsItemSelected(item)
    }
}