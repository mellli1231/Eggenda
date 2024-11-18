package com.example.eggenda.ui.settings


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.eggenda.LocaleHelper
import com.example.eggenda.R
import com.example.eggenda.ui.account.LoginActivity

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{
    companion object {
        const val DIALOG_KEY = "dialog"
        const val TERMS_CONDITIONS_DIALOG = 0
        const val LOGOUT_DIALOG = 1
    }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //load UI
        setPreferencesFromResource(R.xml.preference, rootKey)

        //initialize sharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor = sharedPreferences.edit()

        //get user profile bar
        val profilePref: Preference? = findPreference("user_profile")
        //if clicked, direct to profile settings
        profilePref?.setOnPreferenceClickListener {
            println("user profile bar clicked")
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
            true
        }

        val language: Preference? = findPreference("language")
        language?.setOnPreferenceChangeListener { preference, newValue ->
            val languageCode = newValue as String
            println("languageCode: $languageCode")
            editor.putString("language", languageCode)
            editor.apply()

            if(LocaleHelper.getLanguage(requireContext()) != languageCode) {
                println("changing language")
                println("calling localeHelper from settings")
                LocaleHelper.setLocale(requireContext(), languageCode)
                Toast.makeText(requireActivity(), "Language changed", Toast.LENGTH_SHORT).show()
                requireActivity().recreate()
            }
            true
        }

        val termsConditions: Preference? = findPreference("terms")
        termsConditions?.setOnPreferenceClickListener {
            println("terms and conditions clicked")
            showMyDialogFragment(TERMS_CONDITIONS_DIALOG)

            true
        }

        val logout: Preference? = findPreference("logout")
        logout?.setOnPreferenceClickListener {
            println("logout button clicked")
            showMyDialogFragment(LOGOUT_DIALOG)

            true
        }
    }

    class MyRunsDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //get arguments and dialog details
            lateinit var ret: Dialog
            val bundle = arguments
            val dialogId = bundle?.getInt(DIALOG_KEY)
            val builder = AlertDialog.Builder(requireActivity())

            if (dialogId == TERMS_CONDITIONS_DIALOG) {
                val view: View = requireActivity().layoutInflater.inflate(
                    R.layout.dialog_terms_conditions,
                    null
                )
                builder.setView(view)
                builder.setTitle(R.string.conditions_header)

                builder.setNegativeButton(R.string.cancel_button, this)
                ret = builder.create()
            }
            else if (dialogId == LOGOUT_DIALOG) {
                val view: View = requireActivity().layoutInflater.inflate(
                    R.layout.dialog_logout,
                    null
                )
                builder.setView(view)
                builder.setTitle(R.string.log_out)

                //get sharedPreferences for account
                val logoutBtn: Button = view.findViewById(R.id.logout_button)
                val cancelBtn: Button = view.findViewById(R.id.cancel_button)
                val sharedPreferences : SharedPreferences =
                    requireActivity().getSharedPreferences("account", Context.MODE_PRIVATE)

                //if confirm logout
                logoutBtn.setOnClickListener {
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", false) //logout user
                    editor.apply()

                    Toast.makeText(requireActivity(), "Logging Out", Toast.LENGTH_SHORT).show()

                    //redirect to login page
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                //dismiss if cancel button clicked
                cancelBtn.setOnClickListener {
                    dismiss()
                }

                ret = builder.create()
            }

            return ret
        }

        //function when buttons are clicked
        override fun onClick(dialog: DialogInterface, item: Int) {
            if(item == DialogInterface.BUTTON_NEGATIVE) {
                println("fragment negative button clicked")
            }
        }
    }

    //show dialog fragment
    private fun showMyDialogFragment(dialogType: Int) {
        val myDialog = MyRunsDialogFragment()
        val bundle = Bundle().apply {
            putInt(DIALOG_KEY, dialogType)
        }
        myDialog.arguments = bundle
        myDialog.show(parentFragmentManager, "my_dialog")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        //needed for fragment to run
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}