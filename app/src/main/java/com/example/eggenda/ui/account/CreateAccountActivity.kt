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
import com.example.eggenda.ui.database.userDatabase.User
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

class CreateAccountActivity: AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var passwordError: TextView
    private lateinit var confirmError: TextView
    private lateinit var usernameError: TextView
    private lateinit var createAccBtn: Button

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDatabaseDao
    private lateinit var userViewModel: UserViewModel
    private lateinit var repository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //initialize variables
        username = findViewById(R.id.edit_username)
        createAccBtn = findViewById(R.id.create_acc_btn)
        password = findViewById(R.id.create_password)
        confirmPassword = findViewById(R.id.confirm_password)
        passwordError = findViewById(R.id.password_error)
        confirmError = findViewById(R.id.confirm_error)
        usernameError = findViewById(R.id.username_error)

        //initialize database and operations
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDatabaseDao
        repository = UserRepository(databaseDao)
        userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        //create account username and password
        createAccBtn.setOnClickListener {
            val user = username.text.toString()
            val pw = password.text.toString()
            val confirm = confirmPassword.text.toString()
            var isValid = true

            //clear errors
            password.setBackgroundResource(R.drawable.edit_text_border)
            passwordError.visibility = View.GONE
            confirmPassword.setBackgroundResource(R.drawable.edit_text_border)
            confirmError.visibility = View.GONE
            username.setBackgroundResource(R.drawable.edit_text_border)
            usernameError.visibility = View.GONE

            //error if username is empty
            if(user.isEmpty()) {
                usernameError.text = getString(R.string.username_is_empty)
                usernameError.visibility = View.VISIBLE
                username.setBackgroundResource(R.drawable.error_text_border)
                println("username is empty")
                isValid = false
            }
            //error if password is empty
            if(pw.isEmpty()) {
                passwordError.text = getString(R.string.password_empty)
                passwordError.visibility = View.VISIBLE
                password.setBackgroundResource(R.drawable.error_text_border)
                println("password is empty")
                isValid = false
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
            }

            //move on if get valid inputs
            if(isValid) {
                CoroutineScope(Dispatchers.IO).launch {
                    //check if username already exists
                    val isExists = repository.userExists(user)
                    withContext(Dispatchers.Main) {
                        if(isExists) {
                            usernameError.text = getString(R.string.username_already_exists)
                            usernameError.visibility = View.VISIBLE
                            username.setBackgroundResource(R.drawable.error_text_border)
                            isValid = false
                            println("username already exists")
                        }
                    }

                    //create account if username doesn't exist
                    if(isValid) {
                        val hashedPassword = BCrypt.hashpw(pw, BCrypt.gensalt())
                        val createdUser = User(username = user, password = hashedPassword, points = 0)
                        val id = repository.insert(createdUser)

                        withContext(Dispatchers.Main) {
                            println("Account created with ID: $id, Username: $user")

                            //store current id to access throughout app and after app restart
                            UserPref.setIdUser(this@CreateAccountActivity, user, id)
                            // Navigate to the next activity
                            val intent = Intent(this@CreateAccountActivity, CreateProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}
