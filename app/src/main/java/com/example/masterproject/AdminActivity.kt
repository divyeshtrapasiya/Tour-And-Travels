package com.example.masterproject

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.masterproject.databinding.ActivityAdminBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID


class AdminActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminBinding
    lateinit var reference: DatabaseReference



    lateinit var filePath: Uri

    lateinit var dowmloadurl: Uri

    // request code
    private val PICK_IMAGE_REQUEST = 22

    // instance for firebase storage and StorageReference
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initview()
    }

    private fun initview() {


        reference = FirebaseDatabase.getInstance().reference



        //---------------------------image upload--------------------------------------------
        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(
            Color.parseColor("#0F9D58")
        )
        actionBar?.setBackgroundDrawable(colorDrawable)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()


        binding.pakageimage.setOnClickListener {
            SelectImage()




        }


        // on pressing btnUpload uploadImage() is called
        binding.txtuploadimage.setOnClickListener {

            uploadImage()

        }



        binding.rtlDisplay.setOnClickListener {


            if (TextUtils.isEmpty(binding.edtpakage.text.toString())) {
                Toast.makeText(this, "please enter pakage", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(binding.edtname.text.toString())) {
                Toast.makeText(this, "please enter name", Toast.LENGTH_SHORT).show()

            }
            else if (TextUtils.isEmpty(binding.edtprice.text.toString())) {
                Toast.makeText(this, "please enter price", Toast.LENGTH_SHORT).show()

            }
            else if (TextUtils.isEmpty(binding.edtdays.text.toString())) {
                Toast.makeText(this, "please enter days", Toast.LENGTH_SHORT).show()

            }
            else if (TextUtils.isEmpty(binding.edtmobile.text.toString())) {
                Toast.makeText(this, "please enter Mobile", Toast.LENGTH_SHORT).show()

            }
            else if (TextUtils.isEmpty(binding.edtnotes.text.toString())) {
                Toast.makeText(this, "please enter notes", Toast.LENGTH_SHORT).show()

            }
            else if (TextUtils.isEmpty(binding.edtdiscription.text.toString())) {
                Toast.makeText(this, "please enter discription", Toast.LENGTH_SHORT).show()

            }
            else {


                var pakage = binding.edtpakage.text.toString()
                var name = binding.edtname.text.toString()
                var price = binding.edtprice.text.toString().toInt()
                var days = binding.edtdays.text.toString()
                var mobile = binding.edtmobile.text.toString()
                var notes = binding.edtnotes.text.toString()
                var discription = binding.edtdiscription.text.toString()
                var image = dowmloadurl.toString()


                var key = reference.child("PakageTb").push().key ?: "  "

                var model = PakageModel(key, pakage, name, price, days,mobile, notes,discription, image)
                reference.child("PakageTb").child(key).setValue(model).addOnCompleteListener {


                    if (it.isSuccessful) {
                        Toast.makeText(this, "record successfully", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

                }.addOnFailureListener {

                    Log.e("TAG", "initview: " + it.message)
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()


                }

            }

        }
    }


    private fun uploadImage() {

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()



            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "images/"
                            + UUID.randomUUID().toString()
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()

                    ref.downloadUrl.addOnSuccessListener {


                        dowmloadurl = it
                        Log.e("TAG", "uploadImage: " + it)

                    }

                }.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->

                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }


    }

    private fun SelectImage() {





        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            // Get the Uri of data
            filePath = data.data!!
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                binding.pakageimage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }

}



class PakageModel {

    var id = ""
    var pakage = ""
    var name = ""
    var price = 0
    var days = ""
    var mobile = ""
    var notes = ""
    var discription = ""
    var imageUrl = ""


    constructor(
        id: String,
        pakage: String,
        name: String,
        price: Int,
        days: String,
        mobile: String,
        notes: String,
        discription: String,
        imageUrl: String


    ) {
        this.id = id
        this.pakage = pakage
        this.name = name
        this.price = price
        this.days = days
        this.mobile = mobile
        this.notes = notes
        this.discription = discription
        this.imageUrl = imageUrl

    }

    constructor() {


    }


}
