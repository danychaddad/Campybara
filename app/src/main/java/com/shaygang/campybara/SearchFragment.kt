package com.shaygang.campybara

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        recyclerView.addItemDecoration(verticalSpaceItemDecoration)
        val campsiteIdList = arrayListOf<String>()
        Campsite.getCampsiteIds(campsiteIdList) {
            val myAdapter = SearchAdapter(it!!, requireContext())
            recyclerView.adapter = myAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
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