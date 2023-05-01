package com.shaygang.campybara

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.databinding.ActivityCreateCampsiteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Integer.parseInt
import java.util.*


class CreateCampsiteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCampsiteBinding
    private lateinit var fragment: MapsFragment
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campsite)
        supportActionBar?.title = "Create Campsite"
        binding = ActivityCreateCampsiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fragment = MapsFragment()
                // perform any long-running operations here, such as loading data from a network or database
                supportFragmentManager.beginTransaction().replace(R.id.mapLayout, fragment).commit()
            }
        }

        binding.saveLocationBtn.setOnClickListener {
            binding.geolocationTextView.text = MapsFragment.CAMPSITE_ADDRESS
        }

        binding.confirmBtn.setOnClickListener {
            var name = binding.csNameET.text.toString()
            var description = binding.csDescET.text.toString()
            var capacity : Int
            var address = binding.geolocationTextView.text.toString()

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Name and description cannot be blank!", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    capacity = parseInt(binding.csCapET.text.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Enter a value for capacity!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (capacity < 1) {
                    Toast.makeText(this, "Capacity must be greater than 0!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (address.isEmpty()) {
                        Toast.makeText(this, "Save your campsite's location !!", Toast.LENGTH_SHORT).show()

                    } else {
                        uploadImageToFirebaseStorage()
                    }
                }
            }
        }

        binding.selectPhoto.setOnClickListener {
            chooseImage()
        }
    }

    private fun exitCampsiteCreation() {
        finish()
    }
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val drawable = BitmapDrawable(resources,bitmap)
                binding.selectPhotoView.setImageBitmap(bitmap)
                binding.selectPhoto.alpha = 0f
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_campsite/${filename}.jpg")

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true)

            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            ref.putBytes(data)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        confirmCampsiteCreation(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("CreateCampsiteActivity", "Image could not be uploaded !!")
                }
        } else {
            confirmCampsiteCreation("gs://campybara-f185f.appspot.com/images_campsite/campy.jpg")
        }
    }
    private fun confirmCampsiteCreation(imageUrl: String) {
        var name = binding.csNameET.text.toString()
        var description = binding.csDescET.text.toString()
        var capacity = parseInt(binding.csCapET.text.toString())
        var location = arrayListOf<Double>(MapsFragment.CAMPSITE_LOCATION.latitude, MapsFragment.CAMPSITE_LOCATION.longitude)

            val ref = FirebaseDatabase.getInstance().getReference("campsites")
            val campsite = Campsite(name, description, capacity, imageUrl, 2.5, FirebaseAuth.getInstance().currentUser!!.uid, location)
            ref.push().setValue(campsite).addOnSuccessListener {
                Toast.makeText(this,"Successfully added campsite!", Toast.LENGTH_SHORT).show()

                exitCampsiteCreation()
        }
    }
}