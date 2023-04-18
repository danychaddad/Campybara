package com.shaygang.campybara

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shaygang.campybara.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        user = FirebaseAuth.getInstance().currentUser!!
                        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                        databaseRef.get().addOnCompleteListener {res ->
                            if (res.isSuccessful) {
                                if (res.result.exists()) {
                                    val dataSnapshot = res.result
                                    val firstName = dataSnapshot.child("firstName").value.toString()
                                    val lastName = dataSnapshot.child("lastName").value.toString()

                                    Toast.makeText(this, "Welcome $firstName $lastName !!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Sign In Unsuccessful !!", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
        binding.resetPassBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().setLanguageCode("en") // Set to English
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sent password reset email !!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Invalid email address !!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Insert email address !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}