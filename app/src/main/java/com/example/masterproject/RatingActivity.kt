package com.example.masterproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.masterproject.databinding.ActivityRatingBinding

class RatingActivity : AppCompatActivity() {

    lateinit var binding: ActivityRatingBinding

    var RatingValue : Float = 0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initview()

    }

    private fun initview() {


        binding.btnratingsubmit.setOnClickListener{

            RatingValue = binding.rating.rating

            Toast.makeText(applicationContext, "Rating is "+RatingValue, Toast.LENGTH_SHORT).show()

            val i = Intent(this,DashBoardActivity::class.java)
            startActivity(i)

        }

    }
}