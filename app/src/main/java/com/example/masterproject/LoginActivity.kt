package com.example.masterproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.masterproject.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var refrence: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase


    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE)

        initview()

    }

    @SuppressLint("RestrictedApi")
    private fun initview() {

        auth = Firebase.auth

        refrence = FirebaseDatabase.getInstance().reference

        firebaseDatabase = FirebaseDatabase.getInstance()


        textChangeListner()


        if (sharedPreferences.getBoolean("isLogin", false) == true) {
            var intent = Intent(this@LoginActivity, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.linlogin.setOnClickListener {

            CheckAccountCreateOrNot()

        }


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1044954131245-jluvutpq1t8fug8d14sflcver40fid95.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)
        binding.googlesignin.setOnClickListener { // Initialize sign in intent
            val intent: Intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }


        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(
                Intent(
                    this@LoginActivity,
                    DashBoardActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }



        binding.txtsignup.setOnClickListener {

            var i = Intent(this, Sign_UpActivity::class.java)
            startActivity(i)

        }

    }

    private fun textChangeListner() {

        binding.edtUsername.addTextChangedListener {

            binding.txtusername.error=""


        }

        binding.edtemail.addTextChangedListener {

            binding.txtemail.error=""


        }

        binding.edtPassword.addTextChangedListener {

            binding.txtpassword.error=""


        }
    }


    @SuppressLint("ApplySharedPref")
    private fun CheckAccountCreateOrNot() {



        binding.progresslogin.visibility = View.VISIBLE

        var Username = binding.edtUsername.text.toString()
        var email = binding.edtemail.text.toString()
        var password = binding.edtPassword.text.toString()

        if (Username.isBlank()) {
           binding.txtusername.error="please enter username"
        } else if (email.isBlank()) {
            binding.txtemail.error="please enter email"
        } else if (password.isBlank()) {
            binding.txtpassword.error="please enter password"
        } else {
            auth = FirebaseAuth.getInstance()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()

                    firebaseDatabase.reference.root.child("UserTb").child(Username)
                        .addValueEventListener(object : ValueEventListener {

                            override fun onDataChange(snapshot: DataSnapshot) {

                                binding.progresslogin.visibility = View.GONE
                                var user = snapshot.getValue(Profiles::class.java)

                                sharedPreferences =
                                    getSharedPreferences("MySharedPreferences", MODE_PRIVATE)

                                val imageDownloadUrl = user?.imageUrl
                                var usertype = user?.UserAdmin

                                var sharedPreferences: SharedPreferences.Editor =
                                    sharedPreferences.edit()
                                sharedPreferences.putBoolean("isLogin", true)
                                sharedPreferences.putString("email", email)
                                sharedPreferences.putString("username", Username)
                                sharedPreferences.putString("imageDownloadUrl", imageDownloadUrl)
                                usertype?.let { it1 -> sharedPreferences.putInt("UserAdmin", it1) }

                                sharedPreferences.commit()

                                Log.d("TAG", "onDataChange:==> $user")

                                var i = Intent(this@LoginActivity, DashBoardActivity::class.java)
                                startActivity(i)
                                finish()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("TAG", "onCancelled: " + error.message)
                            }
                        })
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            if (signInAccountTask.isSuccessful) {
                val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                if (googleSignInAccount != null) {
                    val authCredential: AuthCredential =
                        GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                    // Check if user already exists
                    FirebaseAuth.getInstance()
                        .fetchSignInMethodsForEmail(googleSignInAccount.email!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val signInMethods = task.result?.signInMethods
                                if (signInMethods != null && signInMethods.isNotEmpty()) {
                                    // User already exists, sign in
                                    firebaseAuth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(this) { signInTask ->
                                            if (signInTask.isSuccessful) {
                                                // Sign in successful
                                                startActivity(
                                                    Intent(
                                                        this@LoginActivity,
                                                        DashBoardActivity::class.java
                                                    )
                                                )
                                            } else {
                                                // Sign in failed
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Sign in failed: ${signInTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    // User doesn't exist, create a new account
                                    firebaseAuth.createUserWithEmailAndPassword(
                                        googleSignInAccount.email!!,
                                        null.toString()
                                    )
                                        .addOnCompleteListener(this) { createAccountTask ->
                                            if (createAccountTask.isSuccessful) {
                                                // Account created successfully
                                                firebaseAuth.signInWithCredential(authCredential)
                                                    .addOnCompleteListener(this) { signInTask ->
                                                        if (signInTask.isSuccessful) {
                                                            // Sign in successful
                                                            startActivity(
                                                                Intent(
                                                                    this@LoginActivity,
                                                                    DashBoardActivity::class.java
                                                                )
                                                            )
                                                        } else {
                                                            // Sign in failed
                                                            Toast.makeText(
                                                                this@LoginActivity,
                                                                "Sign in failed: ${signInTask.exception?.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                // Account creation failed
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Account creation failed: ${createAccountTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            } else {
                                // Error fetching sign-in methods
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error fetching sign-in methods: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                // Sign in with Google failed
                Toast.makeText(this@LoginActivity, "Sign in with Google failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}


