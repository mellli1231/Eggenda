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
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.MainActivity
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.example.eggenda.ui.database.userDatabase.UserViewModel
import com.example.eggenda.ui.database.userDatabase.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var createAcc: TextView
    private lateinit var userError: TextView
    private lateinit var passwordError: TextView

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDatabaseDao
    private lateinit var userViewModel: UserViewModel
    private lateinit var repository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username_input)
        password = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        createAcc = findViewById(R.id.create_account_link)
        userError = findViewById(R.id.username_error)
        passwordError = findViewById(R.id.password_error)

        //initialize database and operations
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDatabaseDao
        repository = UserRepository(databaseDao)
        userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)

        //once decide to login
        loginBtn.setOnClickListener {
            val user = username.text.toString()
            val pw = password.text.toString()

            //clear errors
            username.setBackgroundResource(R.drawable.edit_text_border)
            userError.visibility = View.GONE
            password.setBackgroundResource(R.drawable.edit_text_border)
            passwordError.visibility = View.GONE

            //if user empty
            if(user.isEmpty()) {
                userError.text = getString(R.string.username_is_empty)
                userError.visibility = View.VISIBLE
                username.setBackgroundResource(R.drawable.error_text_border)
                println("username is empty")
            }
            //is password empty
            if(pw.isEmpty()) {
                passwordError.text = getString(R.string.password_empty)
                passwordError.visibility = View.VISIBLE
                password.setBackgroundResource(R.drawable.error_text_border)
                println("password is empty")
            }

            //check database for more errors
            CoroutineScope(Dispatchers.IO).launch {
                val match = repository.getUser(user) //get user
                withContext(Dispatchers.Main) {
                    //if user doesn't exist
                    if (match == null) {
                        println("User doesn't exist in database")
                        userError.text = getString(R.string.user_doesn_t_exist)
                        userError.visibility = View.VISIBLE
                        username.setBackgroundResource(R.drawable.error_text_border)
                    } else {
                        val isCorrect = BCrypt.checkpw(pw, match.password)
                        //if password is correct
                        if (isCorrect) {
                            println("Password matches")
                            //use id to get sharedPreferences
                            val id = match.id
                            val sharedPreferences = getSharedPreferences("user_${id}", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.apply()

                            //start app
                            Toast.makeText(
                                this@LoginActivity,
                                "Welcome ${user}!",
                                Toast.LENGTH_SHORT
                            ).show()

                            //store username and id in singleton to be accessed throughout the app
                            UserPref.setIdUser(this@LoginActivity, user, id)
                            println("Logging in username: ${user}, id: ${id}")

                            //start app
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            println("password doesn't match")
                            passwordError.text = getString(R.string.incorrect_password)
                            passwordError.visibility = View.VISIBLE
                            password.setBackgroundResource(R.drawable.error_text_border)
                        }
                    }
                }
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