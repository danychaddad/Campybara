package com.shaygang.campybara

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ChatFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        view?.findViewById<Button>(R.id.openChat)?.setOnClickListener {
            fetchUser("pierre@gmail.com")
        }
        return view
    }

    companion object {
        val USERNAME_KEY = "USERNAME_KEY"
        val USERID_KEY = "USERID_KEY"
        val USERPIC_KEY = "USERPIC_KEY"
    }

    private fun fetchUser(userEmail: String) {
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("users").orderByChild("email").equalTo(userEmail)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val firstName = child.child("firstName").value.toString()
                    val lastName = child.child("lastName").value.toString()
                    val userId = child.child("uid").value.toString()
                    val profileUrl = child.child("profileImageUrl").value.toString()

                    val userName = "$firstName $lastName"
                    val intent = Intent(activity, ChatActivity::class.java)
                    intent.putExtra(USERPIC_KEY, profileUrl)
                    intent.putExtra(USERNAME_KEY, userName)
                    intent.putExtra(USERID_KEY, userId)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}