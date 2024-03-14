package com.example.masterproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.masterproject.databinding.ActivityBookPakageBinding
import java.util.Calendar

class BookPakageActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookPakageBinding
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookPakageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initview()

    }

    @SuppressLint("SetTextI18n")
    private fun initview() {


        sharedPreferences =
            getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        val to = sharedPreferences.getString("pakage", "")
        val mobile = sharedPreferences.getString("mobile", "")



        binding.edtto.setText(to)
        binding.edtemail.setText(email)
        binding.edtusername.setText(username)
        binding.edtMobile.setText(mobile)


        var date = binding.edtdate.text.toString()
        val editor = sharedPreferences.edit()
        editor.putString("date", date)
        editor.apply()

        binding.rtlNext.setOnClickListener {

            val from = binding.edtfrom.text.toString()
            val person = binding.edtperson.text.toString()


            if (binding.edtfrom.text!!.isBlank()) {
                binding.edtfrom.error = "Please enter From"
            } else if (binding.edtto.text!!.isBlank()) {
                binding.edtto.error = "Please enter To"
            } else if (binding.edtusername.text!!.isBlank()) {
                binding.edtusername.error = "Please enter Username"
            } else if (binding.edtemail.text!!.isBlank()) {
                binding.edtemail.error = "Please enter Email"

            } else if (binding.edtMobile.text!!.isBlank()) {
                binding.edtMobile.error = "Please enter Mobile"
            } else if (binding.edtdate.text!!.isBlank()) {
                Toast.makeText(this, "Please enter Date", Toast.LENGTH_SHORT).show()
            } else if (binding.edtperson.text!!.isBlank()) {
                binding.edtperson.error = "Please enter Person"
            } else {



                val i = Intent(this, PaymentActivity::class.java)
                i.putExtra("date",date)
                i.putExtra("from",from)
                i.putExtra("person",person)
                startActivity(i)


            }


        }



        binding.edtdate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a date picker dialog
            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in a toast
                val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                binding.edtdate.setText(selectedDate)
                date = selectedDate
                Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
            }, year, month, day)


            dpd.show()
        }
    }
}