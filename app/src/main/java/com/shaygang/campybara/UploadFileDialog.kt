package com.shaygang.campybara

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UploadFileDialog(private val context: Context): DialogFragment() {

    interface UploadFileDialogListener {
        fun onFileChosen(fileUri: Uri?)
    }

    private lateinit var chooseFileButton: Button
    private lateinit var progressBar: ProgressBar
    private var listener: UploadFileDialogListener? = null
    private var fileUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_FILE_CHOOSER = 1
        private const val REQUEST_CODE_PERMISSIONS = 1
        private const val READ_EXTERNAL_STORAGE = 1
    }


    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Apply For Owner")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_apply_owner, null)
        chooseFileButton = view.findViewById(R.id.choose_file_button)
        progressBar = view.findViewById(R.id.progress_bar)
        builder.setView(view)

        chooseFileButton.setOnClickListener {
            chooseFileButton.text = "yo"
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, start the file chooser activity
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_FILE_CHOOSER)
            } else {
                // Permission has not been granted, request it
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_PERMISSIONS)
                }
            }
        }

        // Send Owner Request
        builder.setPositiveButton("Yes") { dialog, which ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val ref = FirebaseDatabase.getInstance().getReference("owner_requests")

            if (uid != null) {
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Check if the user's uid already exists in the node
                        if (snapshot.hasChild(uid)) {
                            // User's uid already exists, do not send new request
                            Toast.makeText(context, "Request already sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            // User's uid does not exist, send new request
                            val requestMap = HashMap<String, Any>()
                            requestMap["isApproved"] = false
                            ref.child(uid).setValue(requestMap)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to send request: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to check if request exists: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onStart() {
        super.onStart()
        // Set the click listener for the choose file button

        // Disable the positive button until a file is chosen
        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
        positiveButton.setOnClickListener {
            listener?.onFileChosen(fileUri)
            progressBar.visibility = View.VISIBLE
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, start the file chooser activity
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
            } else {
                // Permission has been denied, show an error message or request the permission again
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == RESULT_OK) {
            data?.data?.let { fileUri ->
                // Update the UI and enable the positive button
                chooseFileButton.text = fileUri.lastPathSegment
                val dialog = dialog as AlertDialog
                val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
                positiveButton.isEnabled = true
            }
        }
    }

    fun setListener(listener: UploadFileDialogListener) {
        this.listener = listener
    }
}