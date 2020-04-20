package com.shubh.covid19tracker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val query = intent.getStringExtra("name")

        GlobalScope.launch(Dispatchers.Main) {
            val thisCountry = withContext(Dispatchers.IO) { Client.api.getUserDet(query) }
            Picasso.get().load(thisCountry.countryInfo?.flag).into(imgFlag)
            toolbarDet.setTitle(thisCountry.country)
            contName.text = thisCountry.country
            totCases.text = "Total Cases : " + thisCountry.cases.toString()
            actCases.text = "Active Cases : " + thisCountry.active.toString() + " ↑"+thisCountry.todayCases.toString()
            recoveredCases.text = "Recovered Cases : " + thisCountry.recovered.toString()
            deadCases.text = "Deceased : " + thisCountry.deaths.toString() + " ↑"+thisCountry.todayDeaths.toString()
            criticalCases.text = "Critical Cases : "+thisCountry.critical
            Tests.text="Total Test : "+thisCountry.tests
            TestsPM.text = "Tests Per Million : "+thisCountry.testsPerOneMillion

            val myFormat = "h:mm a, d MMM YYYY"
            val sdf = SimpleDateFormat(myFormat)
            val dateEDT = sdf.format(thisCountry.updated)

            lastUpdt.text = "Last Updated : " + dateEDT
        }
    }
}
