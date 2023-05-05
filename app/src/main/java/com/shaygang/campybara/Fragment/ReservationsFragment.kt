package com.shaygang.campybara

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.shaygang.campybara.Activity.CreateGroupActivity
import com.shaygang.campybara.Activity.GroupActivity
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

        val scanButton = view.findViewById<FloatingActionButton>(R.id.scanButton)

        scanButton.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)

            integrator.setOrientationLocked(false)
            integrator.setBeepEnabled(false)
            integrator.setPrompt("")
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)

            integrator.initiateScan()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Scan cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Scan successful: ${result.contents}", Toast.LENGTH_SHORT).show()
                val newGroupKey = result.contents
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid != null) {
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("memberOf")
                    userRef.child(newGroupKey).setValue(newGroupKey)

                    val groupRef = FirebaseDatabase.getInstance().getReference("groups/$newGroupKey/memberList")
                    val memberList: MutableList<String> = mutableListOf()

                    groupRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            memberList.clear()
                            for (childSnapshot in snapshot.children) {
                                val memberId = childSnapshot.getValue(String::class.java)

                                if (memberId != null) {
                                    if (uid == memberId) {
                                        return
                                    } else {
                                        memberList.add(memberId)
                                    }

                                }
                            }
                            memberList.add(uid)
                            groupRef.setValue(memberList)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                }

                fetchGroups()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fetchGroups() {
        groupList.clear()

        val database = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val queryMember = database.child("/users/$uid/memberOf")
        val queryCreator = database.child("/users/$uid/creatorOf")

        queryMember.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val groupUid = child.key.toString()
                        groupList.add(groupUid)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        queryCreator.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val groupUid = child.key.toString()
                        groupList.add(groupUid)
                    }

                    val adapter = GroupAdapter<GroupieViewHolder>()

                    for (groupUid in groupList) {
                        adapter.add(GroupItem(groupUid, requireContext()))
                    }
                    view?.findViewById<RecyclerView>(R.id.groupListView)?.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    companion object {
        val GROUPNAME_KEY = "GROUPNAME_KEY"
        val GROUPID_KEY = "GROUPID_KEY"
        val GROUPPIC_KEY = "GROUPPIC_KEY"
        val GROUPCREATOR_KEY = "GROUPCREATOR_KEY"
    }
}

class GroupItem(val uid : String, val context: Context): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val database = FirebaseDatabase.getInstance().reference.child("/groups/$uid")
        database.get().addOnSuccessListener {
            val groupName = it.child("name").value.toString()
            val groupPic = it.child("groupImageUrl").value.toString()
            val groupCreator = it.child("creator").value.toString()

            Picasso.get().load(groupPic).into(viewHolder.itemView.findViewById<ImageView>(R.id.groupPic))
            viewHolder.itemView.findViewById<TextView>(R.id.groupName).text = "$groupName"

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(context, GroupActivity::class.java)
                intent.putExtra(ReservationsFragment.GROUPPIC_KEY, groupPic)
                intent.putExtra(ReservationsFragment.GROUPNAME_KEY, groupName)
                intent.putExtra(ReservationsFragment.GROUPID_KEY, uid)
                intent.putExtra(ReservationsFragment.GROUPCREATOR_KEY, groupCreator)
                context.startActivity(intent)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.group_row
    }
}