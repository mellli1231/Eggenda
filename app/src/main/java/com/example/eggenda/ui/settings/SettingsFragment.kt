package com.example.eggenda.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.eggenda.R
import com.example.eggenda.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat(){
    companion object {
        const val METRIC = 0
        const val IMPERIAL = 1

        const val DIALOG_KEY = "dialog"
        const val STRING_DIALOG = 2
        const val UNIT_DIALOG = 3
    }

    private var isAnonymous: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //load UI
        addPreferencesFromResource(R.xml.preference)

        //initialize sharedPreferences and load settings
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor = sharedPreferences.edit()
        loadPreferences()
        println("saved settings loaded")

        //get user profile bar
        val profilePref: Preference? = findPreference("user_profile")
        //if clicked, direct to profile settings
        profilePref?.setOnPreferenceClickListener {
            println("user profile bar clicked")
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
            true
        }
    }

    private fun loadPreferences() {

    }
}