package com.shubh.covid19tracker

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.theme_dialog.view.*

class SettingsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val sharedPreferencesEditor by lazy {
        sharedPreferences.edit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeTheme(false)
        setContentView(R.layout.activity_settings)

        themeBtn.setOnClickListener {
            openDialog()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key != null && key == DARK_THEME) {
                changeTheme(true)
            }
        }

    }

    private fun changeTheme(reCreate: Boolean) {
        if(sharedPreferences.getBoolean(DARK_THEME, true)){
            setTheme(R.style.DarkTheme)
        }else{
            setTheme(R.style.LightTheme)
        }
        if(reCreate){
            recreate()
        }
    }

    private fun openDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.theme_dialog, null, false)


        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setCancelable(true)
        val mAlertDialog = mBuilder.show()



        mDialogView.radioButtonDark.setOnClickListener {
            mAlertDialog.dismiss()
//            Toast.makeText(this,"Something",Toast.LENGTH_SHORT).show()
            sharedPreferencesEditor.putBoolean(DARK_THEME, true).commit()
            setResult(THEME_CHANGED)
        }

        mDialogView.radioButtonLight.setOnClickListener {
            mAlertDialog.dismiss()
//            Toast.makeText(this,"Other thing",Toast.LENGTH_SHORT).show()
            sharedPreferencesEditor.putBoolean(DARK_THEME, false).commit()
            setResult(THEME_CHANGED)
        }
    }

}
