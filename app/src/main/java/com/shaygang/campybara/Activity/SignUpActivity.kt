package com.shaygang.campybara.Activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.Class.User
import com.shaygang.campybara.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream
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

        binding.signInTxt.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.signUnBtn.setOnClickListener {
            performRegister()
        }

        binding.dateOfBirth.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateOfBirth(myCalendar)
            }
            val maxDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -13)
            }.timeInMillis

            val dialog = DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.maxDate = maxDate
            dialog.show()
        }

    }

    private fun exitSignUpActivity() {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun updateDateOfBirth(myCalendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
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

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNb.isNotEmpty() && dateOfBirth.isNotEmpty()
            && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                         firebaseAuth.currentUser!!.sendEmailVerification()
                        uploadImageToFirebaseStorage()
                    } else {
                        Toast.makeText(this, "Email taken or invalid !!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (firstName.isEmpty()) {
                binding.firstName.error = "Field cannot be empty"
            }
            if (lastName.isEmpty()) {
                binding.lastName.error = "Field cannot be empty"
            }
            if (phoneNb.isEmpty()) {
                binding.phoneNumber.error = "Field cannot be empty"
            }
            if (dateOfBirth.isEmpty()) {
                binding.dateOfBirth.error = "Field cannot be empty"
            }
            if (email.isEmpty()) {
                binding.emailEt.error = "Field cannot be empty"
            }
            if (pass.isEmpty()) {
                binding.passET.error = "Field cannot be empty"
            }
            if (confirmPass.isEmpty()) {
                binding.confirmPassEt.error = "Field cannot be empty"
            }
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_profile/${filename}.jpg")

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true)

            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            ref.putBytes(data)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
                }
        } else {
            saveUserToFirebaseDatabase("https://firebasestorage.googleapis.com/v0/b/campybara-f185f.appspot.com/o/images_profile%2Fdefault_pp.jpg?alt=media&token=b2dc37b3-51f6-46f8-995f-231a779410a2")
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
        val isOwner = false
        val isAdmin = false

        val user = User(uid, firstName, lastName, phoneNb, dateOfBirth, email, profileImageUrl, isAdmin, isOwner)
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign up successful. Check your inbox to verify your email!", Toast.LENGTH_LONG).show()
                exitSignUpActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "User not saved in database !!", Toast.LENGTH_SHORT).show()
            }
    }
}