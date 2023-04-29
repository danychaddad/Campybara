package com.shaygang.campybara

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ChatFragment : Fragment() {

    var chatList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        fetchChats()

        return view
    }

    private fun fetchChats() {
        val database = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val query = database.child("/user_messages/$uid")

        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val chatUid = child.key.toString()
                        chatList.add(chatUid)
                    }
                    val adapter = GroupAdapter<GroupieViewHolder>()

                    for (chatUid in chatList) {
                        adapter.add(ChatItem(chatUid, context!!))
                    }
                    view?.findViewById<RecyclerView>(R.id.chatListView)?.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    companion object {
        val USERNAME_KEY = "USERNAME_KEY"
        val USERID_KEY = "USERID_KEY"
        val USERPIC_KEY = "USERPIC_KEY"
    }
}

class ChatItem(val uid : String, val context: Context): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val database = FirebaseDatabase.getInstance().reference.child("/users/$uid")
        database.get().addOnSuccessListener {
            val chatFirstName = it.child("firstName").value.toString()
            val chatLastName = it.child("lastName").value.toString()
            val chatProfilePic = it.child("profileImageUrl").value.toString()
            val chatUserName = "$chatFirstName $chatLastName"

            Picasso.get().load(chatProfilePic).into(viewHolder.itemView.findViewById<ImageView>(R.id.chatUserPic))
            viewHolder.itemView.findViewById<TextView>(R.id.chatUserName).text = "$chatUserName"

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(ChatFragment.USERPIC_KEY, chatProfilePic)
                intent.putExtra(ChatFragment.USERNAME_KEY, chatUserName)
                intent.putExtra(ChatFragment.USERID_KEY, uid)
                context.startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}