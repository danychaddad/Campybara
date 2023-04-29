package com.shaygang.campybara

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth

class ReservationsFragment : Fragment() {

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

        return view
    }
}