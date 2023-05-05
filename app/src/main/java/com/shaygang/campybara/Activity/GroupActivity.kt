package com.shaygang.campybara.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.shaygang.campybara.Adapter.ReservationAdapter
import com.shaygang.campybara.ChatFragment
import com.shaygang.campybara.Reservation
import com.shaygang.campybara.ReservationState
import com.shaygang.campybara.ReservationsFragment
import com.shaygang.campybara.databinding.ActivityGroupBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


class GroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupBinding
    val adapter = GroupAdapter<GroupieViewHolder>()
    private var groupId: String? = null
    private var groupCreatorId: String? = null
    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val groupName = intent.getStringExtra(ReservationsFragment.GROUPNAME_KEY)
        supportActionBar?.title = groupName

        groupId = intent.getStringExtra(ReservationsFragment.GROUPID_KEY)
        groupCreatorId = intent.getStringExtra(ReservationsFragment.GROUPCREATOR_KEY)

        binding.chatGroupCreator.setOnClickListener {
            groupCreatorId?.let { it1 -> fetchUser(it1) }
        }
        if (groupCreatorId == FirebaseAuth.getInstance().currentUser?.uid) {
            binding.chatGroupCreator.isVisible = false
        } else {
            binding.groupJoinCode.isVisible = false
        }

        binding.groupJoinCode.setOnClickListener {
            showQrCodeDialog(groupId!!)
        }
        loadReservations()
    }

    private fun loadReservations() {
        Reservation.getReservationsForGroup(groupId!!) {
            val pendingReservations = arrayListOf<String>()
            val pendingLayoutManager = LinearLayoutManager(this)
            val pendingAdapter = ReservationAdapter(pendingReservations, this)
            val pendingRecycler = binding.groupInfoPendingReservationsRecycler
            pendingRecycler.layoutManager = pendingLayoutManager
            pendingRecycler.adapter = pendingAdapter
            val acceptedReservations = arrayListOf<String>()
            val acceptedLayoutManager = LinearLayoutManager(this)
            val acceptedAdapter = ReservationAdapter(acceptedReservations,this)
            val acceptedRecycler = binding.groupInfoAcceptedReservationsRecycler
            acceptedRecycler.layoutManager = acceptedLayoutManager
            acceptedRecycler.adapter = acceptedAdapter
            val rejectedReservations = arrayListOf<String>()
            val rejectedAdapter = ReservationAdapter(rejectedReservations,this)
            val rejectedRecycler = binding.groupInfoRejectedRecycler
            val rejectedLayoutManager = LinearLayoutManager(this)
            rejectedRecycler.layoutManager = rejectedLayoutManager
            rejectedRecycler.adapter = rejectedAdapter

            for (id in it!!) {
                var res : Reservation
                Reservation.getReservationById(id) {
                    res = it!!
                    when (res.reservationState) {
                        ReservationState.PENDING -> {
                            pendingReservations.add(id)
                            pendingAdapter.notifyDataSetChanged()
                        }
                        ReservationState.ACCEPTED -> {
                            acceptedReservations.add(id)
                            acceptedAdapter.notifyDataSetChanged()
                        }
                        ReservationState.REJECTED -> {
                            rejectedReservations.add(id)
                            rejectedAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun showQrCodeDialog(groupId: String) {

        val hintMap = HashMap<EncodeHintType, Any>()
        hintMap[EncodeHintType.MARGIN] = 0
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(groupId, BarcodeFormat.QR_CODE, 500, 500, hintMap)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        val builder = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        builder.setView(imageView)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun fetchUser(uid: String) {
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child("users").orderByChild("uid").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val firstName = child.child("firstName").value.toString()
                    val lastName = child.child("lastName").value.toString()
                    val userId = child.child("uid").value.toString()
                    val profileUrl = child.child("profileImageUrl").value.toString()

                    val userName = "$firstName $lastName"
                    val intent = Intent(this@GroupActivity, ChatActivity::class.java)
                    intent.putExtra(ChatFragment.USERPIC_KEY, profileUrl)
                    intent.putExtra(ChatFragment.USERNAME_KEY, userName)
                    intent.putExtra(ChatFragment.USERID_KEY, userId)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
