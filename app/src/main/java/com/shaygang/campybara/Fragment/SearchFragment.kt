package com.shaygang.campybara

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.shaygang.campybara.Adapter.SearchAdapter
import com.shaygang.campybara.Class.Campsite
import com.shaygang.campybara.Class.User

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spacingHeight = resources.getDimensionPixelSize(R.dimen.vertical_spacing_height)
        val verticalSpaceItemDecoration = VerticalSpaceItemDecoration(spacingHeight)
        val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val toggleButton = view.findViewById<Switch>(R.id.favoritesSwitch)
        recyclerView.addItemDecoration(verticalSpaceItemDecoration)
        recyclerView.layoutManager = LinearLayoutManager(context)
        var campsiteIdList = arrayListOf<String>()
        var searchAdapter : SearchAdapter = SearchAdapter(arrayListOf(), requireContext())
        Campsite.getCampsiteIds(campsiteIdList) {
            searchAdapter.setCampsites(it!!)
            recyclerView.adapter = searchAdapter
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                User.getUserFavorites(FirebaseAuth.getInstance().currentUser!!.uid) {
                    campsiteIdList = it!!
                    searchAdapter.setCampsites(it!!)
                    recyclerView.adapter = searchAdapter
                }
            } else {
                Campsite.getCampsiteIds(campsiteIdList) {
                    campsiteIdList = it!!
                    searchAdapter.setCampsites(it!!)
                    recyclerView.adapter = searchAdapter
                }
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Hide the keyboard when the user submits their search query
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (recyclerView.adapter as SearchAdapter).filterCampsiteList(campsiteIdList, newText.orEmpty())
                return true
            }
        })
    }
}

class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        // Add top margin to all items except the first item
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = verticalSpaceHeight
        }
    }
}