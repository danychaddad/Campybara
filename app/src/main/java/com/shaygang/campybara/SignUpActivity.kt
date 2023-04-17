package com.shaygang.campybara

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.databinding.ActivitySignUpBinding
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.button.setOnClickListener {
            performRegister()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.dateOfBirth.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONDAY, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateOfBirth(myCalendar)
            }
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun updateDateOfBirth(myCalendar: Calendar) {
        val dateFormat = "dd-mm-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        binding.dateOfBirth.setText(sdf.format(myCalendar.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            binding.selectPhotoView.setImageBitmap(bitmap)
            binding.selectPhoto.alpha = 0f
        }
    }

    private fun performRegister() {
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val phoneNb = binding.phoneNumber.text.toString()
        val dateOfBirth = binding.dateOfBirth.text.toString()
        val email = binding.emailEt.text.toString()
        val pass = binding.passET.text.toString()
        val confirmPass = binding.confirmPassEt.text.toString()

        if (selectedPhotoUri != null && firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNb.isNotEmpty() && dateOfBirth.isNotEmpty()
            && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        uploadImageToFirebaseStorage()
                    } else {
                        Toast.makeText(this, "Email taken or invalid !!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_profile/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image could not be uploaded !!", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users")

        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val phoneNb = binding.phoneNumber.text.toString()
        val dateOfBirth = binding.dateOfBirth.text.toString()
        val email = binding.emailEt.text.toString()

        val user = User(uid, firstName, lastName, phoneNb, dateOfBirth, email, profileImageUrl)
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Up Successful !!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "User not saved in database !!", Toast.LENGTH_SHORT).show()
            }
    }
}

class User(val uid: String, val firstName: String, val lastName: String, val phoneNb: String, val dateOfBirth: String,
           val email: String, val profileImageUrl: String)