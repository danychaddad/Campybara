package com.shaygang.campybara

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaygang.campybara.Adapter.SearchAdapter
import com.shaygang.campybara.Class.Campsite

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
        recyclerView.addItemDecoration(verticalSpaceItemDecoration)
        val campsiteIdList = arrayListOf<String>()
        var searchAdapter : SearchAdapter
        Campsite.getCampsiteIds(campsiteIdList) {
            searchAdapter = SearchAdapter(it!!, requireContext())
            recyclerView.adapter = searchAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Hide the keyboard when the user submits their search query
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                (recyclerView.adapter as SearchAdapter).filterCampsiteList(newText.orEmpty())
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