package com.shaygang.campybara

import android.content.Context
import android.graphics.Outline
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

class HomeFragment : Fragment() {
    private lateinit var adapter: CampsiteAdapter
    private lateinit var recyclerView: RecyclerView
    private var campsiteIdList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CampsiteAdapter(campsiteIdList, requireContext())
        recyclerView.adapter = adapter
        campsiteInitialize()
    }

    private fun campsiteInitialize() {
        Log.d("Checking","your mom is gay")
        Campsite.getCampsiteIds(campsiteIdList) {
            Log.d("list",it.toString())
            adapter.notifyDataSetChanged()
        }
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
