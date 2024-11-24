package com.example.eggenda.ui.settings


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.eggenda.MyViewModel
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.Util
import com.example.eggenda.ui.database.userDatabase.UserDatabase
import com.example.eggenda.ui.database.userDatabase.UserDatabaseDao
import com.example.eggenda.ui.database.userDatabase.UserRepository
import com.example.eggenda.ui.database.userDatabase.UserViewModel
import com.example.eggenda.ui.database.userDatabase.UserViewModelFactory
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ProfileActivity: AppCompatActivity() {
    companion object {
        const val DIALOG_KEY = "dialog"
        const val PHOTO_DIALOG = 4
    }


    private lateinit var userName: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var emailPhone: EditText
    private lateinit var dateBirth: EditText
    private lateinit var country: EditText
    private lateinit var saveProfile: Button
    private lateinit var cancelProfile: Button
    private lateinit var usernameError: TextView


    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var tempImgUri: Uri
    private lateinit var imgUri: Uri
    private lateinit var myViewModel: MyViewModel
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private val tempImgFileName = "temp.jpg"
    private val imgFileName = "profile.jpg"
    private var profileImagePath: String? = null
    private lateinit var tempImgFile: File
    private lateinit var profileImgFile: File
    private var isPhotoChange: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var galleryResult: ActivityResultLauncher<Intent>


    private var ogUser: String? = ""
    private var id: String? = ""
    private lateinit var database: UserDatabase
    private lateinit var databaseDao: UserDatabaseDao
    private lateinit var userViewModel: UserViewModel
    private lateinit var repository: UserRepository
    private lateinit var userViewModelFactory: UserViewModelFactory
    private lateinit var FBdatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)


        //initialize variables
        userName = findViewById(R.id.edit_username)
        firstName = findViewById(R.id.edit_first_name)
        lastName = findViewById(R.id.edit_last_name)
        emailPhone = findViewById(R.id.edit_email_phone)
        dateBirth = findViewById(R.id.edit_birth)
        country = findViewById(R.id.edit_country)
        imageView = findViewById(R.id.profile_photo)
        button = findViewById(R.id.btnChangePhoto)
        saveProfile = findViewById(R.id.save_profile)
        cancelProfile = findViewById(R.id.cancel_profile)
        usernameError = findViewById(R.id.username_error)


        //get user data
        ogUser = UserPref.getUsername(this)
        id = UserPref.getId(this)
        sharedPreferences = getSharedPreferences("user_${id}", MODE_PRIVATE)


        //initialize database and operations
        FBdatabase = FirebaseDatabase.getInstance()
        myRef = FBdatabase.reference.child("users")
        database = UserDatabase.getInstance(this)
        databaseDao = database.userDatabaseDao
        repository = UserRepository(databaseDao)
        userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)


        //retrieve saved data on startup
        loadProfile()
        println("profile successfully loaded onto screen")


        //Profile Photo setup
        Util.checkPermissions(this)


        tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this, "com.example.eggenda", tempImgFile)


        profileImgFile = File(getExternalFilesDir(null), imgFileName)
        imgUri = FileProvider.getUriForFile(this, "com.example.eggenda", profileImgFile)


        //implement button prompting camera
        button.setOnClickListener() {
            println("change profile photo button clicked")
            showMyDialogFragment(PHOTO_DIALOG, "Pick Profile Picture")
        }


        //initialize gallery activity result
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                //get uri of image
                result.data?.data?.let { uri ->
                    //read image through input stream
                    val inputStream = contentResolver.openInputStream(uri)
                    //write image to tempImgFile
                    tempImgFile.outputStream().use { outputStream->
                        inputStream?.copyTo(outputStream)
                    }
                    //create bitmap
                    val bitmap = Util.getBitmap(this, tempImgUri)
                    setProfilePhoto(bitmap)
                    println("Image successfully loaded onto screen")
                }
            }
        }


        //initialize gallery activity result
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                println("first camera result call")
                //read image details and create bitmap
                val exifInterface = ExifInterface(tempImgFile.absolutePath)
                val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                val bitmap = Util.getBitmap(this, tempImgUri)
                println("orientation: $orientation")


                //rotate when needed
                val matrix = Matrix()
                when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_NORMAL, 1 -> {}
                    else -> {
                        matrix.postRotate(0f)
                    }
                }
                //create correct orientation of image
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                setProfilePhoto(rotated)
            }
        }


        //
        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.userImage.observe(this) {
            imageView.setImageBitmap(it)
            println("new photo set to view")
        }


        //save button feature
        saveProfile.setOnClickListener {
            //only save if username is not empty
            if(userName.text.toString().isNotEmpty()) {
                saveProfile()
                println("profile saved")
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                usernameError.text = getString(R.string.username_is_empty)
                usernameError.visibility = View.VISIBLE
                userName.setBackgroundResource(R.drawable.error_text_border)
                println("username is empty")
            }
        }


        //cancel button feature
        cancelProfile.setOnClickListener {
            //don't save any changes
            println("not saving profile")
            finish()
        }
    }


    class MyRunsDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //get arguments and dialog details
            lateinit var ret: Dialog
            val bundle = arguments
            val dialogId = bundle?.getInt(DIALOG_KEY)
            val title = bundle?.getString("TITLE") ?: "My Title"
            val builder = AlertDialog.Builder(requireActivity())


            if(dialogId == PHOTO_DIALOG) {
                val view: View = requireActivity().layoutInflater.inflate(
                    R.layout.dialog_photo,
                    null
                )
                builder.setView(view)
                builder.setTitle(title)


                //get buttons
                val galleryBtn : Button = view.findViewById(R.id.chooseGallery)
                val photoBtn : Button = view.findViewById(R.id.choosePhoto)


                //open gallery if button pressed
                galleryBtn.setOnClickListener{
                    (activity as? ProfileActivity)?.openGallery()
                    dismiss()
                }


                //open camera if button pressed
                photoBtn.setOnClickListener{
                    (activity as? ProfileActivity)?.takePhoto()
                    dismiss()
                }
                ret = builder.create()
            }
            return ret
        }


        //function when buttons are clicked
        override fun onClick(dialog: DialogInterface, item: Int) {
            if(item == Dialog.BUTTON_POSITIVE) {
                Toast.makeText(activity, "Input Saved", Toast.LENGTH_LONG).show()
                println("fragment positive button clicked")
            }
            if(item == DialogInterface.BUTTON_NEGATIVE) {
                Toast.makeText(activity, "Input cancelled", Toast.LENGTH_LONG).show()
                println("fragment negative button clicked")
            }
        }
    }


    //show dialog fragment
    private fun showMyDialogFragment(dialogType: Int, title: String) {
        val myDialog = MyRunsDialogFragment()
        val bundle = Bundle().apply {
            putInt(DIALOG_KEY, dialogType)
            putString("TITLE", title)
        }
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "my_dialog")
    }


    //function to open gallery and call activity result to display photo
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)
        println("gallery opened")
    }


    //function to open camera and take photo then call activity result to display photo
    fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        cameraResult.launch(intent)
        println("camera launched")
    }


    //function to set profile photo onto screen
    private fun setProfilePhoto(bitmap: Bitmap) {
        println("set profile photo")
        myViewModel.userImage.value = bitmap
        isPhotoChange = true
        imageView.setImageBitmap(bitmap)
    }


    //function to load saved profile to screen
    private fun loadProfile() {
        profileImagePath = sharedPreferences.getString("profileImagePath", null)


        //load saved data
        if (profileImagePath != null) {
            val ogFile = File(profileImagePath!!)
            if (ogFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(ogFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            }
        }


        //get saved data
        val savedUsername = sharedPreferences.getString("username", "")
        val savedfirstName = sharedPreferences.getString("firstName", "")
        val savedlastName = sharedPreferences.getString("lastName", "")
        val savedEmailPhone = sharedPreferences.getString("emailPhone", "")
        val savedBirth = sharedPreferences.getString("dateBirth", "")
        val savedCountry = sharedPreferences.getString("country", "")


        //load data onto screen
        userName.setText(savedUsername)
        firstName.setText(savedfirstName)
        lastName.setText(savedlastName)
        emailPhone.setText(savedEmailPhone)
        dateBirth.setText(savedBirth)
        country.setText(savedCountry)
    }


    //function to save profile settings
    private fun saveProfile() {
        //extract inputted data
        val username = userName.text.toString()
        val first = firstName.text.toString()
        val last = lastName.text.toString()
        val ep = emailPhone.text.toString()
        val birth = dateBirth.text.toString()
        val country2 = country.text.toString()


        if (username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        //only update file path if a photo was taken
        if(isPhotoChange) {
            tempImgFile.copyTo(profileImgFile, overwrite = true)
            profileImagePath = profileImgFile.path
        }


        //log profile values
        println("username: $username")
        println("first name: $first")
        println("last name: $last")
        println("email/phone: $ep")
        println("date of birth: $birth")
        println("country: $country2")


        //save changes
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("firstName", first)
        editor.putString("lastName", last)
        editor.putString("emailPhone", ep)
        editor.putString("dateBirth", birth)
        editor.putString("country", country2)
        editor.putString("profileImagePath", profileImagePath)
        editor.apply()

        //update singleton
        UserPref.updateUsername(this, username)

        //update database
        CoroutineScope(Dispatchers.IO).launch {
            id?.let { repository.updateUsername(username, it)}
        }

        id?.let { userId ->
            val userRef = myRef.child(userId)  // Reference to the specific user node

            // Set the updated user data
            val usernameUpdate = hashMapOf<String, Any>(
                "username" to username
            )
            userRef.updateChildren(usernameUpdate).addOnCompleteListener{task ->
                if(task.isSuccessful) {
                    println("Username updated in firebase")
                } else {
                    println("Failed to update username: ${task.exception?.message}")
                }
            }
        }

    }
}
