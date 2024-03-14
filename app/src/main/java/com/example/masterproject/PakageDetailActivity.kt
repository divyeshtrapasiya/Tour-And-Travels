package com.example.masterproject

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.masterproject.Adapter.ImageSliderAdapter
import com.example.masterproject.databinding.ActivityPakageDetailBinding

class PakageDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityPakageDetailBinding
    var list : ArrayList<Int> = ArrayList()
    lateinit var txtYes : TextView
    lateinit var txtNo : TextView
    lateinit var txtMobiles : TextView
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPakageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initview()

    }

    private fun initview() {

        var pakage = intent.getStringExtra("pakage")
        var name = intent.getStringExtra("name")
        var price = intent.getIntExtra("Price",0)
        var days = intent.getStringExtra("day")
        var mobile = intent.getStringExtra("mobile")
        var notes = intent.getStringExtra("Notes")
        var discription = intent.getStringExtra("discription")


        val sharedPref = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("pakage", pakage.toString())
        editor.putString("Price", price.toString())
        editor.putString("mobile", mobile.toString())

        editor.apply()


        binding.txtpakage.text = pakage
        binding.txtname.text = name
        binding.txtprice.text = price.toString()
        binding.txtdays.text = days.toString()
        binding.txtmobile.text = mobile
        binding.txtnotes.text = notes
        binding.txtdiscription.text = discription

        list.add(R.drawable.adventure)
        list.add(R.drawable.adventure1)
        list.add(R.drawable.adventure4)
        list.add(R.drawable.adventure5)
        list.add(R.drawable.adventure2)

        var adaptor = ImageSliderAdapter(list,this)
        binding.viewpager.adapter=adaptor


        binding.rtlNext.setOnClickListener{

            var i = Intent(this,BookPakageActivity::class.java)
            startActivity(i)


        }

        val dialog = Dialog(this@PakageDetailActivity)
        binding.imgcall.setOnClickListener {

            dialog.setContentView(R.layout.phone_call)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            dialog.window?.attributes?.windowAnimations = R.style.animation

            txtYes = dialog.findViewById(R.id.txtyes)
            txtNo = dialog.findViewById(R.id.txtno)
            txtMobiles = dialog.findViewById(R.id.txtmobile)

            txtMobiles.text = mobile
            txtYes.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$mobile")
                startActivity(intent)
            }

            txtNo.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            }



    }
}