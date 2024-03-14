package com.example.masterproject

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.masterproject.databinding.ActivityPaymentBinding
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONException
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(),PaymentResultWithDataListener {

    lateinit var binding: ActivityPaymentBinding
    lateinit var sharedPreferences: SharedPreferences
    var Price = String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

        initview()

    }

    private fun initview() {


        var date = intent.getStringExtra("date")
        var from = intent.getStringExtra("from")
        var person = intent.getStringExtra("person")

        binding.txtdate.text = date
        binding.txtfrom.setText(from)
        binding.txtperson.setText(person)


        var Price = sharedPreferences.getString("Price", "")
        val to = sharedPreferences.getString("pakage", "")


        binding.txttourprice.text = Price
        binding.txttotal.text = Price
        binding.txtto.text = to


        binding.rtlBooktour.setOnClickListener {

            /*
                *  You need to pass the current activity to let Razorpay create CheckoutActivity
                * */
            val activity: Activity = this
            val co = Checkout()

            try {
                val options = JSONObject()
                options.put("name", "Razorpay Corp")
                options.put("description", "Demoing Charges")
                //You can omit the image option to fetch the image from the dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
                options.put("theme.color", "#3399cc");
                options.put("currency", "INR");
                options.put("order_id", "order_DBJOWzybf0sJbb");
                options.put("amount", "50000")

                val retryObj = JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                val prefill = JSONObject()
                prefill.put("email", "gaurav.kumar@example.com")
                prefill.put("contact", "9876543210")

                options.put("prefill", prefill)
                co.open(activity, options)
            } catch (e: Exception) {
                Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }


        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        TODO("Not yet implemented")
    }
}