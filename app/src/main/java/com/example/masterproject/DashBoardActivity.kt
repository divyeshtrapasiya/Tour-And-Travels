package com.example.masterproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.masterproject.Fragment.HomeFragment
import com.example.masterproject.databinding.ActivityDashBoardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashBoardActivity : AppCompatActivity() {


    lateinit var auth: FirebaseAuth


    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityDashBoardBinding

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context


        fun initialize(context: Context) {
            this.context = context
        }

        val sharedPref by lazy {
            context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        }
        val UserAdmin: Int get() = (sharedPref.getInt("UserAdmin", 0) ?: "") as Int

    }

    lateinit var sharedPreferences: SharedPreferences

    lateinit var reference: DatabaseReference

    lateinit var versionTV: TextView
    lateinit var downloadUrl: Uri
    lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Companion.initialize(this)
        initview()


    }

    override fun onResume() {
        super.onResume()
        getFirebaseProfile()
    }

    override fun onStart() {
        super.onStart()
        getFirebaseProfile()
    }

    override fun onRestart() {
        super.onRestart()

        getFirebaseProfile()
    }

    @SuppressLint("SetTextI18n", "ApplySharedPref")
    private fun initview() {


        // version display

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.versionCode

        // Display the version name and version code in TextViews
        binding.txtVersionNumber.text = "Version Name: $versionName"
        binding.txtVersionCode.text = "Version Code:$versionCode"


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        binding.txthome.setOnClickListener {

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame, HomeFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        binding.linhome.setOnClickListener {

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame, HomeFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        binding.linprivecypolicy.setOnClickListener {

            val url = "https://www.termsfeed.com/live/f8b313f9-7708-4060-a751-c8315240801e"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

        }

        binding.linrateapp.setOnClickListener {


            var i = Intent(this, RatingActivity::class.java)
            startActivity(i)
            onBackPressed()

        }

        binding.linlogout.setOnClickListener {

            val builder = AlertDialog.Builder(this@DashBoardActivity)
            builder.setTitle("Log out")
            builder.setMessage("Are you sure log out")
            builder.setPositiveButton(
                "Yes"
            )
            {

                    dialogInterface, i ->
                var myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                myEdit.remove("isLogin")
                myEdit.commit()
                auth.signOut()
                var intent = Intent(this@DashBoardActivity, LoginActivity::class.java)
                Toast.makeText(this@DashBoardActivity, "Successfully Logged Out", Toast.LENGTH_SHORT)
                    .show()
                startActivity(intent)
                finish()
//                Toast.makeText(this@DashBoardActivity, "yes is click", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton(
                "No"
            ) { dialogInterface, i ->
                Toast.makeText(
                    this@DashBoardActivity,
                    "no is click",
                    Toast.LENGTH_SHORT
                ).show()
            }

            builder.setCancelable(false)
            builder.show()

        }

        binding.drower.setOnClickListener {
            binding.drawerlayout.openDrawer(binding.navigation)
        }


        val isNewUser = sharedPref.getBoolean("isLogin", false)


        getFirebaseProfile()

    }



    private fun getFirebaseProfile() {

        firebaseDatabase = FirebaseDatabase.getInstance()

        auth = FirebaseAuth.getInstance()

        auth.currentUser?.let {
            firebaseDatabase.reference.root.child("UserTb").child("username")
                .addValueEventListener(object :
                    ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {

                        sharedPreferences =
                            getSharedPreferences("MySharedPreferences", MODE_PRIVATE)

                        // Replace "your_key" with the actual key you used to store the data
                        val usernames = sharedPreferences.getString("username", "")
                        val emails = sharedPreferences.getString("email", "")
                        val image = sharedPreferences.getString("imageDownloadUrl", "")

                        val usertype = sharedPreferences.getInt("UserAdmin", 0) // Retrieve user type from SharedPreferences

                        Log.e("TAG", "onDataChangess: " + usertype)

                        var sharedPreferences: SharedPreferences.Editor = sharedPreferences.edit()
                        val userTypeText = when (usertype) {
                            0 -> "User"
                            else -> "Admin"
                        }


                        sharedPreferences.putInt("UserAdmin", usertype)



                        binding.txtusername.text = "username: $usernames"
                        binding.txtemail.text = "email: $emails"
                        binding.txtUser.text = "usertype: $userTypeText"
                        Glide.with(this@DashBoardActivity).load(image).into(binding.profileImage)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@DashBoardActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

}
