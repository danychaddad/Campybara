package com.shaygang.campybara.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.shaygang.campybara.Class.Group
import com.shaygang.campybara.MainActivity
import com.shaygang.campybara.R
import com.shaygang.campybara.databinding.ActivityCreateGroupBinding
import com.shaygang.campybara.email
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

    var memberList = mutableListOf<DataSnapshot>()
    var memberUids = mutableListOf<String>()
class CreateGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Create Group"
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Companion.binding = binding

        binding.createGroupBtn.setOnClickListener {
            val name = binding.groupName.text.toString()
            val description = binding.groupDesc.text.toString()
            val members = memberUids.size

            if (name.isEmpty() || description.isEmpty()) {
                if (name.isEmpty()) {
                    binding.groupName.error = "Field cannot be empty"
                }
                if (description.isEmpty()) {
                    binding.groupDesc.error = "Field cannot be empty"
                }
            } else
                if (members < 1) {
                    Toast.makeText(this, "Add at least 1 member to your group !!", Toast.LENGTH_SHORT).show()
                } else {
                    uploadImageToFirebaseStorage()
                }
            }

        binding.selectPhoto.setOnClickListener {
            chooseImage()
        }

        binding.addGroupMember.setOnClickListener {
            val memberEmail = binding.newMemberEmail.text.toString().trim()
            fetchUser(memberEmail)
        }
    }

    private fun fetchUser(memberEmail: String) {
        if(memberEmail.isNotEmpty()) {
            if (email != memberEmail) {
                val database = FirebaseDatabase.getInstance().reference
                val query = database.child("users").orderByChild("email").equalTo(memberEmail)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userExists = dataSnapshot.exists()
                        if (userExists) {
                            for (child in dataSnapshot.children) {
                                val newUid = child.child("uid").value.toString()
                                if(!memberUids.contains(newUid)) {
                                    memberUids.add(newUid)
                                    memberList.add(child)
                                    Toast.makeText(this@CreateGroupActivity, "Member added to group !!", Toast.LENGTH_SHORT).show()
                                    val adapter = GroupAdapter<GroupieViewHolder>()
                                    for (member in memberList) {
                                        adapter.add(UserItem(member))
                                    }
                                    binding.memberListView.adapter = adapter
                                } else {
                                    Toast.makeText(this@CreateGroupActivity, "Member already added !!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@CreateGroupActivity, "User doesn't exist !!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("CreateGroupActivity", "Error adding member !!")
                    }
                })
            }
            else {
                Toast.makeText(this, "Cannot add yourself !!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Add valid member email !!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun exitGroupCreation() {
        memberList = mutableListOf<DataSnapshot>()
        memberUids = mutableListOf<String>()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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
            val ref = FirebaseStorage.getInstance().getReference("/images_groups/${filename}.jpg")

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true)

            val baos = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            ref.putBytes(data)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        confirmGroupCreation(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("CreateGroupActivity", "Image could not be uploaded !!")
                }
        } else {
            confirmGroupCreation("https://firebasestorage.googleapis.com/v0/b/campybara-f185f.appspot.com/o/images_profile%2Fdefault_pp.jpg?alt=media&token=b2dc37b3-51f6-46f8-995f-231a779410a2")
        }
    }

    private fun confirmGroupCreation(imageUrl: String) {
        val name = binding.groupName.text.toString()
        val description = binding.groupDesc.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("groups")
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val group = uid?.let { Group(name, description, it, imageUrl, memberUids) }

        val newGroupKey = ref.push().key
        if (newGroupKey != null) {
            ref.child(newGroupKey).setValue(group).addOnSuccessListener {
                if (uid != null) {
                    memberUids.add(uid)
                }
                for (memberUid in memberUids) {
                    var userRef: DatabaseReference
                    if (memberUid == uid) {
                        userRef = FirebaseDatabase.getInstance().getReference("users").child(memberUid).child("creatorOf")
                    } else {
                        userRef = FirebaseDatabase.getInstance().getReference("users").child(memberUid).child("memberOf")
                    }
                    userRef.child(newGroupKey).setValue(newGroupKey)
                }

                Toast.makeText(this,"Successfully added group !!", Toast.LENGTH_SHORT).show()
                exitGroupCreation()
            }
        }
    }

    companion object {
        private lateinit var binding: ActivityCreateGroupBinding
        fun removeMember() {
            val adapter = GroupAdapter<GroupieViewHolder>()
            for (member in memberList) {
                adapter.add(UserItem(member))
            }

            binding.memberListView.adapter = adapter
        }
    }
}

class UserItem(val member : DataSnapshot) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val memberFirstName = member.child("firstName").value.toString()
        val memberLastName = member.child("lastName").value.toString()
        val memberProfilePic = member.child("profileImageUrl").value.toString()
        Picasso.get().load(memberProfilePic).into(viewHolder.itemView.findViewById<ImageView>(R.id.memberListPic))
        viewHolder.itemView.findViewById<TextView>(R.id.memberListName).text = "$memberFirstName $memberLastName"

        viewHolder.itemView.findViewById<ImageButton>(R.id.removeMember).setOnClickListener {
            val memberId = member.child("uid").value.toString()
            memberUids.remove(memberId)
            memberList.remove(member)
            CreateGroupActivity.removeMember()
        }
    }

    override fun getLayout(): Int {
        return R.layout.new_member_row
    }
}