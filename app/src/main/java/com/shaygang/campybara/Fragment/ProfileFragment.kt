package com.shaygang.campybara.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.Class.User
import com.shaygang.campybara.Activity.CreateCampsiteActivity
import com.shaygang.campybara.R
import com.shaygang.campybara.dateOfBirth
import com.shaygang.campybara.email
import com.shaygang.campybara.firstName
import com.shaygang.campybara.isAdmin
import com.shaygang.campybara.isOwner
import com.shaygang.campybara.lastName
import com.shaygang.campybara.phoneNb
import com.shaygang.campybara.profileImageUrl
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileFragment : Fragment() {

    private var selectedPhotoUri: Uri? = null
    private var selectedPhotoUrl: String? = null
    private var changedImage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val firstNameField = view?.findViewById<TextView>(R.id.firstName)
        val lastNameField = view?.findViewById<TextView>(R.id.lastName)
        val emailField = view?.findViewById<TextView>(R.id.emailEt)
        val phoneNbField = view?.findViewById<TextView>(R.id.phoneNumber)
        val dateOfBirthField = view?.findViewById<TextView>(R.id.dateOfBirth)
        val photoField = view?.findViewById<Button>(R.id.updatePhoto)
        val photoView = view?.findViewById<ImageView>(R.id.updatePhotoView)
        val removeProfilePic = view?.findViewById<ImageView>(R.id.removeProfilePic)

        if (isOwner == false) {
            view.findViewById<Button>(R.id.btnNewCampsite).isVisible = false
        }

        view.findViewById<Button>(R.id.btnNewCampsite)?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MapsFragment.LOCATION_REQUEST_CODE
                )

            } else {
                // Check if GPS is enabled
                val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // GPS is not enabled, show a dialog to ask the user to enable it
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Enable GPS")
                        .setMessage("Please enable GPS to create a campsite.")
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
                    val intent = Intent(activity, CreateCampsiteActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        firstNameField?.text = isAdmin.toString()
        lastNameField?.text = isOwner.toString()
        emailField?.text = email
        phoneNbField?.text = phoneNb
        dateOfBirthField?.text = dateOfBirth

        if (photoView != null) {
            Glide.with(this)
                .load(profileImageUrl)
                .into(photoView)
            photoField?.alpha = 0f
        }

        photoField?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        removeProfilePic?.setOnClickListener {
            changedImage = false
            selectedPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/campybara-f185f.appspot.com/o/images_profile%2Fdefault_pp.jpg?alt=media&token=b2dc37b3-51f6-46f8-995f-231a779410a2"
            if (photoView != null) {
                Glide.with(this)
                    .load(selectedPhotoUrl)
                    .into(photoView)
                photoField?.alpha = 0f
            }

        }

        val updateProfileBtn = view?.findViewById<Button>(R.id.updateProfileBtn)
        updateProfileBtn?.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage("Are you sure?")
                    .setNegativeButton("No") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        if (firstNameField?.text.toString().isNotEmpty() && lastNameField?.text.toString()
                                .isNotEmpty() && phoneNbField?.text.toString().isNotEmpty()
                        ) {
                            if (changedImage) {
                                uploadImageToFirebaseStorage(firstNameField?.text.toString(), lastNameField?.text.toString(), phoneNbField?.text.toString())
                            } else if (selectedPhotoUrl != null) {
                                updateProfile(firstNameField?.text.toString(), lastNameField?.text.toString(), phoneNbField?.text.toString(), selectedPhotoUrl!!)
                            } else {
                                profileImageUrl?.let { it2 ->
                                    updateProfile(firstNameField?.text.toString(), lastNameField?.text.toString(), phoneNbField?.text.toString(), it2)
                                }
                            }
                        } else {
                            firstNameField?.text = firstName
                            lastNameField?.text = lastName
                            phoneNbField?.text = phoneNb
                            if (photoView != null) {
                                Glide.with(this)
                                    .load(profileImageUrl)
                                    .into(photoView)
                                photoField?.alpha = 0f
                            }
                            Toast.makeText(context, "Empty Fields Are Not Allowed !!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .show()
            }
        }

        return view
    }

    private fun deleteOldImageFromFirebaseStorage(profileImageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl)
        storageRef.delete()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photoField = view?.findViewById<Button>(R.id.updatePhoto)
        val photoView = view?.findViewById<ImageView>(R.id.updatePhotoView)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            if (photoView != null) {
                Glide.with(this)
                    .load(selectedPhotoUri)
                    .into(photoView)
                photoField?.alpha = 0f
                changedImage = true
                selectedPhotoUrl = null
            }
        }
    }

    private fun uploadImageToFirebaseStorage(firstNameField: String, lastNameField: String, phoneNbField: String) {
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images_profile/${filename}.jpg")

            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhotoUri)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true)

            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            ref.putBytes(data)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        profileImageUrl?.let { it1 -> deleteOldImageFromFirebaseStorage(it1) }
                        updateProfile(firstNameField, lastNameField, phoneNbField, it.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProfile(newFirstName: String, newLastName: String, newPhoneNb: String, newProfileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users")

        val user =
            dateOfBirth?.let {
                email?.let { it1 ->
                    isAdmin?.let { it2 ->
                        isOwner?.let { it3 ->
                            User(uid, newFirstName, newLastName, newPhoneNb, it, it1, newProfileImageUrl, it2, it3)
                        }
                    }
                }
            }
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                firstName = newFirstName
                lastName = newLastName
                phoneNb = newPhoneNb
                profileImageUrl = newProfileImageUrl
                Toast.makeText(context, "Profile Updated Successfully !!", Toast.LENGTH_SHORT).show()
                changedImage = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Could Not Update Profile !!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()

        val firstNameField = view?.findViewById<TextView>(R.id.firstName)
        val lastNameField = view?.findViewById<TextView>(R.id.lastName)
        val emailField = view?.findViewById<TextView>(R.id.emailEt)
        val phoneNbField = view?.findViewById<TextView>(R.id.phoneNumber)
        val dateOfBirthField = view?.findViewById<TextView>(R.id.dateOfBirth)
        val photoField = view?.findViewById<Button>(R.id.updatePhoto)
        val photoView = view?.findViewById<ImageView>(R.id.updatePhotoView)

        firstNameField?.text = firstName
        lastNameField?.text = lastName
        emailField?.text = email
        phoneNbField?.text = phoneNb
        dateOfBirthField?.text = dateOfBirth
        if (photoView != null) {
            Glide.with(this)
                .load(profileImageUrl)
                .into(photoView)
            photoField?.alpha = 0f
        }
    }
}