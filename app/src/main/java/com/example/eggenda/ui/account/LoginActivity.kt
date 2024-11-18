package com.example.eggenda.ui.account

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eggenda.MainActivity
import com.example.eggenda.R

class LoginActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var createAcc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username_input)
        password = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        createAcc = findViewById(R.id.create_account_link)
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE)

        //once decide to login
        loginBtn.setOnClickListener {
            val user = username.text.toString().trim()
            val pw = password.text.toString().trim()

            //if valid input then save username and password and keep logged in
            if(user.isNotEmpty() && pw.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.putString("username", user)
                editor.putString("password", pw)
                editor.apply()

                //start app
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
            }
        }

        //create account
        createAcc.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}