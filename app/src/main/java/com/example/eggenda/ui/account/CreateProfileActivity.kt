package com.example.eggenda.ui.account

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.InvalidationTracker
import com.example.eggenda.MainActivity
import com.example.eggenda.MyViewModel
import com.example.eggenda.R
import com.example.eggenda.UserPref
import com.example.eggenda.Util
import com.example.eggenda.ui.settings.ProfileActivity
import java.io.File

class CreateProfileActivity: AppCompatActivity() {
    companion object {
        const val DIALOG_KEY = "dialog"
        const val PHOTO_DIALOG = 4
    }

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var emailPhone: EditText
    private lateinit var dateBirth: EditText
    private lateinit var country: EditText
    private lateinit var createAccBtn: Button

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        //initialize variables
        firstName = findViewById(R.id.edit_first_name)
        lastName = findViewById(R.id.edit_last_name)
        emailPhone = findViewById(R.id.edit_email_phone)
        dateBirth = findViewById(R.id.edit_birth)
        country = findViewById(R.id.edit_country)
        imageView = findViewById(R.id.profile_photo)
        button = findViewById(R.id.btnChangePhoto)
        createAccBtn = findViewById(R.id.create_acc_btn)

        //get correlating user
        val id = UserPref.getId(this)
        sharedPreferences = getSharedPreferences("user_${id}", MODE_PRIVATE) //info for user
        println("editing user id: $id")

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

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.userImage.observe(this) {
            imageView.setImageBitmap(it)
            println("new photo set to view")
        }

        //save button feature
        createAccBtn.setOnClickListener {
            //create account only if passwords match
            createProfile()
            println("account created")
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java) //direct to home page
            startActivity(intent)
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

    //function to set profile photo onto screen
    private fun setProfilePhoto(bitmap: Bitmap) {
        println("set profile photo")
        myViewModel.userImage.value = bitmap
        isPhotoChange = true
        imageView.setImageBitmap(bitmap)
    }

    //function to save profile settings
    private fun createProfile() {
        //extract inputted data and username
        val username = UserPref.getUsername(this)
        val first = firstName.text.toString()
        val last = lastName.text.toString()
        val ep = emailPhone.text.toString()
        val birth = dateBirth.text.toString()
        val country2 = country.text.toString()

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
        editor.putBoolean("isLoggedIn", true) //login
        editor.apply()
    }


}
