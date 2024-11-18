package com.example.eggenda.ui.account

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.MainActivity
import com.example.eggenda.R

class CreateAccountActivity: AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var passwordError: TextView
    private lateinit var confirmError: TextView
    private lateinit var createAccBtn: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //initialize variables
        userName = findViewById(R.id.edit_username)
        createAccBtn = findViewById(R.id.create_acc_btn)
        password = findViewById(R.id.create_password)
        confirmPassword = findViewById(R.id.confirm_password)
        passwordError = findViewById(R.id.password_error)
        confirmError = findViewById(R.id.confirm_error)
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE)

        //create account username and password
        createAccBtn.setOnClickListener {
            val pw = password.text.toString()
            val confirm = confirmPassword.text.toString()

            var isValid = true
            //error if password is empty
            if(pw.isEmpty()) {
                passwordError.text = getString(R.string.password_empty)
                passwordError.visibility = View.VISIBLE
                password.setBackgroundResource(R.drawable.error_text_border)
                println("password is empty")
                isValid = false
            } else {
                password.setBackgroundResource(R.drawable.edit_text_border)
                passwordError.visibility = View.GONE
                println("password valid")
            }
             //error if confirmation is empty
            if (confirm.isEmpty()) {
                confirmPassword.setBackgroundResource(R.drawable.error_text_border)
                confirmError.text = getString(R.string.password_empty)
                confirmError.visibility = View.VISIBLE
                println("confirm password is empty")
                isValid = false
            }
            //error if passwords dont match
            else if (confirm != pw) {
                confirmPassword.setBackgroundResource(R.drawable.error_text_border)
                confirmError.text= getString(R.string.passwords_do_not_match)
                confirmError.visibility = View.VISIBLE
                println("confirm password doesn't match")
                isValid = false

            } else{
                confirmPassword.setBackgroundResource(R.drawable.edit_text_border)
                confirmError.visibility = View.GONE
                println("confirm password valid")
            }

            //create account if password is valid
            if(isValid) {
                createAccount()
                println("account created")
                val intent = Intent(this, CreateProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    //function to save profile settings
    private fun createAccount() {
        //extract inputted data
        val username = userName.text.toString()
        val password = password.text.toString()

        //log profile values
        println("username: $username")
        println("password: $password")

        //save changes
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }
}
