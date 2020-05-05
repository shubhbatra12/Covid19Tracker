package com.shubh.covid19tracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.internet_dialog.view.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private val cdDet by lazy {
        ConnectionDetector(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeTheme(false)
        setContentView(R.layout.activity_detail)

        detailActRefresh.setOnRefreshListener {
            setData(contName.text.toString())
            detailActRefresh.isRefreshing=false
        }

        val query = intent.getStringExtra("name")
        setData(query)
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


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setData(query:String) {
        if (cdDet.isConnectingToInternet) {
            GlobalScope.launch(Dispatchers.Main) {
                val thisCountry = withContext(Dispatchers.IO) { Client.api.getUserDet(query) }
                Picasso.get().load(thisCountry.countryInfo?.flag).into(imgFlag)
                toolbarDet.setTitle(thisCountry.country)
                contName.text = thisCountry.country
                totCases.text = "Total Cases : " + thisCountry.cases.toString()
                actCases.text =
                    "Active Cases : " + thisCountry.active.toString() + " ↑" + thisCountry.todayCases.toString()
                recoveredCases.text = "Recovered Cases : " + thisCountry.recovered.toString()
                deadCases.text =
                    "Deceased : " + thisCountry.deaths.toString() + " ↑" + thisCountry.todayDeaths.toString()
                criticalCases.text = "Critical Cases : " + thisCountry.critical
                Tests.text = "Total Test : " + thisCountry.tests
                TestsPM.text = "Tests Per Million : " + thisCountry.testsPerOneMillion

                val myFormat = "h:mm a, d MMM YYYY"
                val sdf = SimpleDateFormat(myFormat)
                val dateEDT = sdf.format(thisCountry.updated)

                lastUpdt.text = "Last Updated : $dateEDT"
            }
        } else {
            openDialogInternet()
        }
    }

    private fun openDialogInternet() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, null, false)


        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setCancelable(true)
        val mAlertDialog = mBuilder.show()



        mDialogView.dialogWifiBtnFilter.setOnClickListener {
            mAlertDialog.dismiss()
            startActivityForResult(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), RC_NETWORK)
        }
        mDialogView.dialogDataBtnFilter.setOnClickListener {
            mAlertDialog.dismiss()
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setClassName(
                "com.android.settings",
                "com.android.settings.Settings\$DataUsageSummaryActivity"
            )
            startActivityForResult(intent, RC_NETWORK)
        }
    }
}
