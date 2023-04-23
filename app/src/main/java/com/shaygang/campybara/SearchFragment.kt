package com.shaygang.campybara

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    lateinit var campsiteNameList: ArrayList<String>
    private lateinit var campsiteArrayList : ArrayList<Campsite>
    //private lateinit var adapter: CampsiteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference("campsites")
        //campsite = arrayOf("forest1", "forest2", "forrestGump", "runForrestRun", "campsite1", "campsite2", "campysite")
        campsiteNameList = arrayListOf()
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
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        listView = view.findViewById(R.id.searchListView)
        searchView = view.findViewById(R.id.searchView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campsiteInitialize()
        val campsiteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, campsiteNameList)
        for (campsite in campsiteArrayList) {
            campsiteNameList.add(campsite.name)
        }
        val campsiteList = mutableListOf<String>()
        //adapter = CampsiteAdapter(campsiteArrayList, context = null)
        listView.adapter = campsiteAdapter
        val database = FirebaseDatabase.getInstance().reference

        database.child("campsites").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val itemName = itemSnapshot.child("name").getValue(String::class.java)
                    if (itemName != null) {
                        campsiteList.add(itemName)
                    }
                }
                campsiteAdapter.addAll(campsiteList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (campsiteNameList.contains(query)){

                    campsiteAdapter.filter.filter(query)

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                campsiteAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun campsiteInitialize() {
        campsiteArrayList = arrayListOf<Campsite>()
        // TODO: Get from database instead of being hard-coded
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//              campsiteArrayList.clear()
                // Get all children of myRef
                for (childSnapshot in dataSnapshot.children) {
                    val campsite = Campsite(childSnapshot.child("imageUrl").value.toString(), childSnapshot.child("name").value.toString())
                    Log.d("DB", campsite.name)
                    campsiteArrayList.add(campsite)
                    // Do something with the child key and value
                }
                //adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}