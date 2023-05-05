package com.shaygang.campybara

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaygang.campybara.databinding.ActivityApproveOwnersBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ApproveOwnersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApproveOwnersBinding
    var ownerRequestsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApproveOwnersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Owner Requests"

        fetchOwnerRequests()
    }

    private fun fetchOwnerRequests() {
        ownerRequestsList.clear()

        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("owner_requests")

        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        if (child.child("isApproved").value == false) {
                            val ownerUid = child.key.toString()
                            ownerRequestsList.add(ownerUid)
                        }
                    }

                    val adapter = GroupAdapter<GroupieViewHolder>()

                    for (ownerUid in ownerRequestsList) {
                        adapter.add(OwnerRequestItem(ownerUid, this@ApproveOwnersActivity))
                    }
                    binding.ownerRequestsListView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

class OwnerRequestItem(val uid : String, val context: Context): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val database = FirebaseDatabase.getInstance().reference.child("/users/$uid")
        database.get().addOnSuccessListener {
            val ownerFirstName = it.child("firstName").value.toString()
            val ownerLastName = it.child("lastName").value.toString()
            val ownerName = "$ownerFirstName $ownerLastName"

            val ownerPic = it.child("profileImageUrl").value.toString()

            Picasso.get().load(ownerPic).into(viewHolder.itemView.findViewById<ImageView>(R.id.requestGroupImage))
            viewHolder.itemView.findViewById<TextView>(R.id.requestGroupName).text = "$ownerName"

            viewHolder.itemView.findViewById<ImageButton>(R.id.confirmReservation).setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage("Are you sure you want to accept this request?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        // User clicked Yes button, make user owner
                        val ref1 = FirebaseDatabase.getInstance().getReference("owner_requests").child(uid)
                        ref1.child("isApproved").setValue(true)
                        ref1.child("approvedBy").setValue(FirebaseAuth.getInstance().currentUser?.uid)

                        val ref2 = FirebaseDatabase.getInstance().getReference("users").child(uid)
                        ref2.child("owner").setValue(true)

                        val intent = Intent(context, ApproveOwnersActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                        Toast.makeText(context, "Request accepted !!", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // User clicked No button, dismiss the dialog
                        dialog.cancel()
                    }

                // Create and show the dialog
                val alert = dialogBuilder.create()
                alert.setTitle("Accept Request")
                alert.show()
            }

            viewHolder.itemView.findViewById<ImageButton>(R.id.rejectReservation).setOnClickListener {

                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage("Are you sure you want to reject this request?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        // User clicked Yes button, delete the node
                        val ref = FirebaseDatabase.getInstance().getReference("owner_requests").child(uid)
                        ref.removeValue()
                        val intent = Intent(context, ApproveOwnersActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                        Toast.makeText(context, "Request rejected !!", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // User clicked No button, dismiss the dialog
                        dialog.cancel()
                    }

                // Create and show the dialog
                val alert = dialogBuilder.create()
                alert.setTitle("Reject Request")
                alert.show()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.owner_request_row
    }
}
