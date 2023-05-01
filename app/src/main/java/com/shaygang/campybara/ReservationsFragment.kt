package com.shaygang.campybara

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
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

class ReservationsFragment : Fragment() {

    var groupList = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservations, container, false)

        if (age!! < 18) {
            view?.findViewById<Button>(R.id.addGroup)?.isVisible = false
        }

        view?.findViewById<Button>(R.id.addGroup)?.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        fetchGroups()

        return view
    }

    private fun fetchGroups() {
        val database = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val query = database.child("/groups/").equalTo("-NTcoHqW14CFRXAab6hM")

        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val groupUid = child.key.toString()
                        groupList.add(groupUid)
                    }
                    val adapter = GroupAdapter<GroupieViewHolder>()

                    for (groupUid in groupList) {
                        adapter.add(GroupItem(groupUid, context!!))
                    }
                    view?.findViewById<RecyclerView>(R.id.groupListView)?.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}

class GroupItem(val uid : String, val context: Context): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val database = FirebaseDatabase.getInstance().reference.child("/groups/-NTcoHqW14CFRXAab6hM")
        database.get().addOnSuccessListener {
            val groupName = it.child("name").value.toString()
            val groupDesc = it.child("desc").value.toString()
            val groupPic = it.child("groupImageUrl").value.toString()

            Picasso.get().load(groupPic).into(viewHolder.itemView.findViewById<ImageView>(R.id.groupPic))
            viewHolder.itemView.findViewById<TextView>(R.id.groupName).text = "$groupName"

//            viewHolder.itemView.setOnClickListener {
//                val intent = Intent(context, ChatActivity::class.java)
//                intent.putExtra(ChatFragment.USERPIC_KEY, chatProfilePic)
//                intent.putExtra(ChatFragment.USERNAME_KEY, chatUserName)
//                intent.putExtra(ChatFragment.USERID_KEY, uid)
//                context.startActivity(intent)
//            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}