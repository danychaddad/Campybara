package com.shaygang.campybara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firstNameField = view?.findViewById<TextView>(R.id.firstName)
        val lastNameField = view?.findViewById<TextView>(R.id.lastName)
        val emailField = view?.findViewById<TextView>(R.id.emailEt)
        val phoneNbField = view?.findViewById<TextView>(R.id.phoneNumber)
        val dateOfBirthField = view?.findViewById<TextView>(R.id.dateOfBirth)

        firstNameField?.text = firstName
        lastNameField?.text = lastName
        emailField?.text = email
        phoneNbField?.text = phoneNb
        dateOfBirthField?.text = dateOfBirth
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

        firstNameField?.text = firstName
        lastNameField?.text = lastName
        emailField?.text = email
        phoneNbField?.text = phoneNb
        dateOfBirthField?.text = dateOfBirth

        val updateProfileBtn = view?.findViewById<TextView>(R.id.updateProfileBtn)
        updateProfileBtn?.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage("Are you sure?")
                    .setNegativeButton("No") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        if (firstNameField?.text.toString().isNotEmpty() && lastNameField?.text.toString()
                                .isNotEmpty() && phoneNbField?.text.toString().isNotEmpty()
                        ) {
                            updateProfile(firstNameField?.text.toString(), lastNameField?.text.toString(), phoneNbField?.text.toString())
                        } else {
                            firstNameField?.text = firstName
                            lastNameField?.text = lastName
                            phoneNbField?.text = phoneNb
                            Toast.makeText(context, "Empty Fields Are Not Allowed !!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .show()
            }
        }

        return view
    }

    private fun updateProfile(newFirstName: String, newLastName: String, newPhoneNb: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users")

        val user =
            dateOfBirth?.let { email?.let { it1 ->
                profileImageUrl?.let { it2 ->
                    User(uid, newFirstName, newLastName, newPhoneNb, it, it1, it2)
                }
            } }
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                firstName = newFirstName
                lastName = newLastName
                phoneNb = newPhoneNb

                Toast.makeText(context, "Profile Updated Successfully !!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Could Not Update Profile !!", Toast.LENGTH_SHORT).show()
            }
    }

}