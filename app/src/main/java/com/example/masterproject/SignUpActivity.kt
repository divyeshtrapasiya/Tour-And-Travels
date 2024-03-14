package com.example.masterproject

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.masterproject.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class Sign_UpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    lateinit var reference: DatabaseReference

    lateinit var radioGroup: RadioGroup

    var selectedRadioButtonId = -1

    var selectedRadioButtonText =""

    var UserAdmin = selectedRadioButtonId

    lateinit var filePath: Uri

    var downloadUrl: Uri? = null

    private val PICK_IMAGE_REQUEST = 22

    lateinit var storage: FirebaseStorage

    lateinit var storageReference: StorageReference

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        initview()

    }

    @SuppressLint("SuspiciousIndentation", "NewApi")
    private fun initview() {
        auth = Firebase.auth

        reference = FirebaseDatabase.getInstance().reference
        radioGroup = findViewById(R.id.radiogroup)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()


        //---------------------------image upload--------------------------------------------
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#0F9D58"))
        actionBar?.setBackgroundDrawable(colorDrawable)


        if (sharedPreferences.getBoolean("isLogin", false) == true) {
            var intent = Intent(this@Sign_UpActivity, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }




        if (FirebaseAuth.getInstance().currentUser != null) {


            var i = Intent(this, DashBoardActivity::class.java)
            startActivity(i)
            finish()

        }


        binding.linsignup.setOnClickListener {

            selectedRadioButtonId = radioGroup.checkedRadioButtonId
            var email = binding.edtEmail.text.toString()


            val key = reference.child("UsertypeTb").push().key ?: ""


            var profile = ""
            if (downloadUrl != null) {
                profile = downloadUrl.toString()
            }


            var password = binding.edtPassword.text.toString()
            var Username = binding.edtUsername.text.toString()
            var address = binding.edtaddress.text.toString()
            var mobile = binding.edtmobile.text.toString()



            textChangeListner()


            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            selectedRadioButtonText = selectedRadioButton.text.toString()
            Log.e("TAG", "UserAdmin ==>" + selectedRadioButtonText)

            when (val index: Int = radioGroup.indexOfChild(selectedRadioButton)) {
                0 -> {
                    selectedRadioButtonId = 0
                }

                1 -> {
                    selectedRadioButtonId = 1
                }
            }

            UserAdmin = selectedRadioButtonId
            Log.e("TAG", "initview: -->   "+selectedRadioButtonId )


            if (binding.edtEmail.text.toString().isBlank()) {

                binding.txtemail.error = "Please Enter Email "

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.error = " Please Enter Valid Email"
            } else if (binding.edtPassword.text.toString().isBlank()) {

                binding.txtpassword.error = "Please Enter Password"
            } else if (validateUsername(Username) != true) {
                binding.txtusername.error = "Please Enter Valid Username"
            } else if (binding.edtaddress.text.toString().isBlank()) {
                binding.txtaddress.error = "Please Enter Address"
            } else if (binding.edtmobile.text.toString().isBlank()) {
                binding.txtmobile.error = "Please Enter Mobile Number"
            }
            else if (radioGroup.checkedRadioButtonId == 0) {
                Toast.makeText(this, "please select usertype", Toast.LENGTH_SHORT).show()
            }
            else {



                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {
                        binding.progressbar.visibility = View.VISIBLE
                        var profiles = Profiles(
                            email,
                            email,
                            password,
                            Username,
                            address,
                            mobile,
                            UserAdmin,
                            profile
                        )

                        saveUserData(Username, email, UserAdmin,address, mobile)

                        reference.root.child("UserTb").child(Username).setValue(profiles)
                            .addOnSuccessListener {

                                binding.progressbar.visibility = View.GONE
                                var i = Intent(this, DashBoardActivity::class.java)
                                i.putExtra("UserAdmin", selectedRadioButtonId)
                                startActivity(i)
                                finish()

                            }.addOnFailureListener {
                                Log.e("TAG", "initview:" + it.message)
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            }

                    }
                }.addOnFailureListener {
                    Log.e("TAG", "initview:-----" + it.message)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }



        binding.profileImage.setOnClickListener {
            selectImage.launch("image/*")
            SelectImage()


        }


        binding.txtuploadimage.setOnClickListener {

            uploadImage()

        }


        binding.txtlogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun textChangeListner() {


        binding.edtEmail.addTextChangedListener {
            binding.txtemail.error = ""
        }
        binding.edtPassword.addTextChangedListener {
            binding.txtpassword.error = ""
        }
        binding.edtUsername.addTextChangedListener {
            binding.txtusername.error = ""
        }
        binding.edtaddress.addTextChangedListener {
            binding.txtaddress.error = ""
        }
        binding.edtmobile.addTextChangedListener {
            binding.txtmobile.error = ""
        }

    }

    fun validateUsername(username: String): Boolean {
        val regex = Regex("^[A-Za-z][A-Za-z0-9_]{7,29}$")
        return regex.matches(username)
    }


    private fun SelectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
    }


    val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveImageUri(uri)
            // Optionally, you can load/display the selected image in the ImageView
            binding.profileImage.setImageURI(uri)
        }
    }


    private fun uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploadi ng
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener { // Image uploaded successfully
                // Dismiss dialog
                progressDialog.dismiss()
                Toast.makeText(this@Sign_UpActivity, "Image Uploaded!!", Toast.LENGTH_SHORT).show()

                ref.downloadUrl.addOnSuccessListener { uri ->

                    downloadUrl = uri
                    Log.e("TAG", "uploadImage: downloadable URL $uri")

//                    saveImageDownloadUrl(uri.toString())

                }
            }

                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast.makeText(this@Sign_UpActivity, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }.addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                }
        }
    }


    private fun saveUserData( username: String,
                             email: String,
                             UserAdmin: Int,
                             address: String,
                             mobile: String
    ) {
        val sharedPref = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("username", username)
        editor.putString("email", email)
        editor.putInt("UserAdmin", UserAdmin)
        editor.putString("address", address)
        editor.putString("mobile", mobile)
        if (downloadUrl != null) {
            editor.putString("imageDownloadUrl", downloadUrl.toString())
        } else {
            editor.putString("imageDownloadUrl", "")
        }
        editor.putBoolean("isLogin", true)

        editor.apply()
    }

    private fun saveImageUri(uri: Uri) {
        val editor = sharedPreferences.edit()
        editor.putString("imageUri", uri.toString())
        editor.apply()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            // Get the Uri of data
            filePath = data.data!!
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                binding.profileImage.setImageBitmap(bitmap)

            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }

}


class Profiles {

    var id = ""
    var email = ""
    var password = ""
    var Username = ""
    var address = ""
    var mobile = ""
    var UserAdmin = 0
    var imageUrl = ""


    constructor(
        id: String,
        email: String,
        password: String,
        Username: String,
        address: String,
        mobile: String,
        UserAdmin: Int,
        imageUrl: String
    ) {
        this.id = id
        this.email = email
        this.password = password
        this.Username = Username
        this.address = address
        this.mobile = mobile
        this.UserAdmin = UserAdmin
        this.imageUrl = imageUrl

    }

    constructor() {

    }

}

