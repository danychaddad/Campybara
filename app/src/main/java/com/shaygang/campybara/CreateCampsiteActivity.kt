package com.shaygang.campybara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shaygang.campybara.databinding.ActivityCreateCampsiteBinding
import java.lang.Integer.parseInt

class CreateCampsiteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCampsiteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campsite)
        binding = ActivityCreateCampsiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener{
            exitCampsiteCreation()
        }

        binding.confirmBtn.setOnClickListener {
            confirmCampsiteCreation()
        }
    }

    private fun exitCampsiteCreation() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun confirmCampsiteCreation() {
        var name = binding.csNameET.text.toString()
        var description = binding.csDescET.text.toString()
        var capacity : Int
        try {
             capacity = parseInt(binding.csCapET.text.toString()) }
        catch (e: NumberFormatException) {
            Toast.makeText(this, "Enter a value for capacity!", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be blank!", Toast.LENGTH_SHORT).show()
        } else
        if (capacity < 1) {
            Toast.makeText(this, "Capacity must be greater than 0!", Toast.LENGTH_SHORT).show()
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("campsites")
            val campsite = Campsite(name, description, capacity)
            ref.push().setValue(campsite).addOnSuccessListener {
                Toast.makeText(this,"Successfully added campsite!", Toast.LENGTH_SHORT).show()
                exitCampsiteCreation()
            }
        }
    }
}

class Campsite(val name: String, val description: String, val capacity : Int)