package com.shaygang.campybara

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.shaygang.campybara.databinding.ActivityChatBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatLogView.adapter = adapter

        val username = intent.getStringExtra(ChatFragment.USERNAME_KEY)
        supportActionBar?.title = username

        listenForMessages()

        binding.sendMessage.setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = intent.getStringExtra(ChatFragment.USERID_KEY).toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid.toString()) {
                        adapter.add(ChatToItem(chatMessage.text))
                    } else {
                        val receiverProfileUrl = intent.getStringExtra(ChatFragment.USERPIC_KEY).toString()
                        adapter.add(ChatFromItem(chatMessage.text, receiverProfileUrl))
                    }
                }
                binding.chatLogView.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                val receiverProfileUrl = intent.getStringExtra(ChatFragment.USERPIC_KEY).toString()
                if (chatMessage != null) {
                    adapter.add(ChatFromItem(chatMessage.text, receiverProfileUrl))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onCancelled(p0: DatabaseError) {}

        })
    }

    private fun performSendMessage() {
        val text = binding.editMessage.text.toString().trim()
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = intent.getStringExtra(ChatFragment.USERID_KEY).toString()

        if (text.isNotEmpty()) {
            val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
            val ref2 = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()

            val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    binding.editMessage.text.clear()
                    binding.chatLogView.scrollToPosition(adapter.itemCount - 1)
                }
            ref2.setValue(chatMessage)
        }

    }

}

class ChatFromItem(val text : String, val receiverProfileUrl: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.chatReceiverTextView).text = text
        Picasso.get().load(receiverProfileUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.chatReceiverImageView))

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text : String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.chatSenderTextView).text = text
        Picasso.get().load(profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.chatSenderImageView))

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}