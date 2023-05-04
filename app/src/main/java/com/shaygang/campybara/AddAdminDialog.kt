package com.shaygang.campybara

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddAdminDialog(private val context: Context) {

    private val database = FirebaseDatabase.getInstance().reference

    @RequiresApi(Build.VERSION_CODES.O)
    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add Admin")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_admin, null)
        builder.setView(view)

        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)

        builder.setPositiveButton("Yes") { dialog, which ->
            val email = emailEditText.text.toString().trim().toLowerCase(Locale.ROOT)
            if (email.isNotEmpty()) {
                checkUser(email)
            } else {
                Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUser(email: String) {
        val query = database.child("users").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        if (childSnapshot != null && isUserEligible(childSnapshot)) {
                            setAdmin(childSnapshot)
                        } else {
                            Toast.makeText(context, "User is not eligible to become an admin", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun isUserEligible(user: DataSnapshot): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val birthDate = LocalDate.parse(user.child("dateOfBirth").value as CharSequence?, formatter)
        val currentDate = LocalDate.now()
        val age = Period.between(birthDate, currentDate).years
        return age >= 18 && user.child("admin").value == false && user.child("owner").value == false
    }

    private fun setAdmin(user: DataSnapshot) {
        user?.key?.let {
            database.child("users").child(it).child("admin").setValue(true).addOnSuccessListener {
                Toast.makeText(context, "User is now an admin", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to set admin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}