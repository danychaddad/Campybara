package com.shaygang.campybara

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.location.Location
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adapter: CampsiteAdapter
    private lateinit var recyclerView: RecyclerView
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var campsiteMap : MutableMap<Campsite,String>
    lateinit var imageId : Array<Int>
    lateinit var names : Array<String>
    private var campsiteList : ArrayList<Campsite> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference("campsites")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campsiteInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CampsiteAdapter(campsiteMap, campsiteList, requireContext())
        recyclerView.adapter = adapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun campsiteInitialize() {
        campsiteMap = mutableMapOf()
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get all children of myRef
                for (childSnapshot in dataSnapshot.children) {
                    val imageUrl = childSnapshot.child("imageUrl").value.toString()
                    val campsiteName = childSnapshot.child("name").value.toString()
                    val ownerUid = childSnapshot.child("ownerUID").value.toString()
                    val location = Location(null)
                    val campsite = Campsite(campsiteName, " ",-1,imageUrl,3.5,ownerUid, location)
                    Log.d("DB", campsite.name)
                    campsiteList.add(campsite)
                    campsiteMap[campsite] = childSnapshot.key.toString()
                    // Do something with the child key and value
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
            }
        })
    }
}

class RoundedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private var cornerRadius = 10f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        cornerRadius = typedArray.getDimension(R.styleable.RoundedImageView_cornerRadius, 10f)
        typedArray.recycle()

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view!!.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true
    }
}
