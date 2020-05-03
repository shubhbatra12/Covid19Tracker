package com.shubh.covid19tracker

import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.internet_dialog.view.*
import kotlinx.android.synthetic.main.theme_dialog.*
import kotlinx.android.synthetic.main.theme_dialog.view.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeBtn.setOnClickListener{
            openDialog()
        }

    }

    private fun openDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.theme_dialog, null,false)


        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setCancelable(true)
        val  mAlertDialog = mBuilder.show()



        mDialogView.radioButtonDark.setOnClickListener {
            mAlertDialog.dismiss()
            Toast.makeText(this,"Something",Toast.LENGTH_SHORT).show()
        }
        mDialogView.radioButtonLight.setOnClickListener {
            mAlertDialog.dismiss()
            Toast.makeText(this,"Other thing",Toast.LENGTH_SHORT).show()
        }
    }

}
