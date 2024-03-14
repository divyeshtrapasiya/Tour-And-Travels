package com.example.masterproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.masterproject.databinding.ActivitySplaceScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class Splace_ScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplaceScreenBinding
    lateinit var sharedPerfrences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplaceScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initview()

    }

    private fun initview() {



        sharedPerfrences = getPreferences(Context.MODE_PRIVATE)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backgroundImage: ImageView = findViewById(R.id.travellogo)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_show)
        backgroundImage.startAnimation(slideAnimation)



            Handler().postDelayed({

                var i = Intent(this@Splace_ScreenActivity, LoginActivity::class.java)
                startActivity(i)
                finish()

            }, 2000)


    }

    private fun isFirstTime(): Boolean {
        val isFirstTime = sharedPerfrences.getBoolean("first", true)

        if (isFirstTime) {

            sharedPerfrences.edit().putBoolean("first", false).apply()
        }

        return isFirstTime

        }


    }
