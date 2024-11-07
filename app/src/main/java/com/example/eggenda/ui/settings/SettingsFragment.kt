package com.example.eggenda.ui.settings


import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.eggenda.LocaleHelper
import com.example.eggenda.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{

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