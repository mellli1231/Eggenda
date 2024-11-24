package com.example.eggenda.ui.account


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.ui.database.userDatabase.User
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserFB
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.example.eggenda.ui.database.userDatabase.UserViewModel
import com.example.eggenda.ui.database.userDatabase.UserViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import kotlin.coroutines.resume


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
    private lateinit var FBdatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference


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
        FBdatabase = FirebaseDatabase.getInstance()
        myRef = FBdatabase.reference.child("users")
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
            if (isValid) {
                lifecycleScope.launch {
                    try {
                        println("start coroutine")
                        // Check if username already exists
                        val isExists = checkUserExist(user)
                        if (isExists != null) {
                            withContext(Dispatchers.Main) {
                                usernameError.text = getString(R.string.username_already_exists)
                                usernameError.visibility = View.VISIBLE
                                username.setBackgroundResource(R.drawable.error_text_border)
                                println("Username already exists")
                            }
                            return@launch // Exit the coroutine early
                        }

                        // Create account if username doesn't exist
                        val hashedPassword = BCrypt.hashpw(pw, BCrypt.gensalt())
                        val idFB = myRef.push().key ?: throw Exception("Failed to generate Firebase ID")

                        val createdUser = User(id = idFB, username = user, password = hashedPassword, points = 0)
                        val createdUserFB = UserFB(id = idFB, username = user, password = hashedPassword, points = 0)

                        // Insert into Room and Firebase
                        repository.insert(createdUser)

                        withContext(Dispatchers.Main) {
                            myRef.child(idFB).setValue(createdUserFB).addOnCompleteListener{task->
                                if(task.isSuccessful) {
                                    println("Account created with ID: $idFB, Username: $user")
                                } else {
                                    println("account not created")
                                }
                            }

                            // Store current ID to access throughout app and after app restart
                            UserPref.setUser(this@CreateAccountActivity, user, idFB, hashedPassword, 0)

                            // Navigate to the next activity
                            val intent = Intent(this@CreateAccountActivity, CreateProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } catch(e: Exception) {
                        withContext(Dispatchers.Main) {
                            // Handle any exceptions that may have occurred
                            Toast.makeText(this@CreateAccountActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        println("Error in coroutine: ${e.message}")
                    }
                }
            }
        }
    }

    suspend fun checkUserExist(username: String): UserFB? {
        return suspendCancellableCoroutine { cont ->
            myRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // A user with this username exists, retrieve the user data
                        for (userSnapshot in snapshot.children) {
                            val existingUser = userSnapshot.getValue(UserFB::class.java) // Convert to User object
                            cont.resume(existingUser) // Return the existing user in the callback
                            return
                        }
                    } else {
                        // No user found with this username
                        cont.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error (if any)
                    println("error checking username")
                    cont.resume(null) // Return null if there is an error
                }
            })
        }

    }
}
