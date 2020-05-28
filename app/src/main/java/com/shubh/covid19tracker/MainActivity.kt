package com.shubh.covid19tracker


import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.shubh.covid19tracker.India.IndiaActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.Tests
import kotlinx.android.synthetic.main.activity_detail.TestsPM
import kotlinx.android.synthetic.main.activity_detail.actCases
import kotlinx.android.synthetic.main.activity_detail.contName
import kotlinx.android.synthetic.main.activity_detail.criticalCases
import kotlinx.android.synthetic.main.activity_detail.deadCases
import kotlinx.android.synthetic.main.activity_detail.imgFlag
import kotlinx.android.synthetic.main.activity_detail.lastUpdt
import kotlinx.android.synthetic.main.activity_detail.recoveredCases
import kotlinx.android.synthetic.main.activity_detail.totCases
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.internet_dialog.view.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

const val DARK_THEME = "DarkTheme"
const val RC_SETTINGS = 111
const val THEME_CHANGED = "ThemeChanged"
const val RC_NETWORK = 222

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<Country>()
    val originalList = arrayListOf<Country>()
    val adapter = CountryAdapter(list)

    private val mAppUnitId: String by lazy {
        "ca-app-pub-5689524874061492~2817334170"
    }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val sharedPreferencesEditor by lazy {
        sharedPreferences.edit()
    }
    private val cd by lazy { ConnectionDetector(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeTheme()
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        countryRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
            fetchData()


        swipeToRefresh.setOnRefreshListener {
            fetchData()
        }

        fab.setOnClickListener{
            startActivity(Intent(
                this,
                IndiaActivity::class.java)
            )
        }

        initializeBannerAd(mAppUnitId)
        loadBannerAd()

    }

    private fun initializeBannerAd(appUnitId: String) {

        MobileAds.initialize(this, appUnitId)

    }

    private fun loadBannerAd() {

        val adRequest = AdRequest.Builder()
            .build()
        adView.loadAd(adRequest)
    }

    private fun changeTheme() {
        if (sharedPreferences.getBoolean(DARK_THEME, true)) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.isIconifiedByDefault=false
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                fetchData()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                fetchData()
                return true
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchUsers(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsMain -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), RC_SETTINGS)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun fetchData() {
        if (cd.isConnectingToInternet) {
            GlobalScope.launch(Dispatchers.Main) {
                swipeToRefresh.isRefreshing = true
                val response = withContext(Dispatchers.IO) { Client.api.getMyUser() }
                if (response.isSuccessful) {
                    response.body()?.let {
                        list.clear()
                        originalList.addAll(it)
                        list.addAll(it)

                        //Sorting based on number of cases
                        originalList.sort()
                        list.sort()

                        adapter.notifyDataSetChanged()
                    }
                }
                swipeToRefresh.isRefreshing = false
            }
        }
        else {
            openDialogInternet()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun searchUsers(query: String) {
        if (query.length >= 2) {
            GlobalScope.launch(Dispatchers.Main) {
                val response = withContext(Dispatchers.IO) { Client.api.getUser(query) }
                if (response.isSuccessful) {
                    response.body()?.let {
                        list.clear()
                        list.addAll(listOf(it))
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        } else {
            fetchData()
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

    private fun openDialogDetail(CountryName: CharSequence) {

        val mDialogViewD = LayoutInflater.from(this).inflate(R.layout.dialog_detail, null, false)


        val mBuilderD = AlertDialog.Builder(this)
            //.setView(mDialogViewD)
            .setCancelable(true)
        val mAlertDialogD = mBuilderD.show()

        GlobalScope.launch(Dispatchers.Main) {
            val thisCountry = withContext(Dispatchers.IO) { Client.api.getUserDet(CountryName as String) }
//            Picasso.get().load(thisCountry.countryInfo?.flag).into(imgFlag)
//            contNameD.text = thisCountry.country
//            totCasesD.text = "Total Cases : " + convertToIndianStandard(thisCountry.cases.toString())
//            actCasesD.text = "Active Cases : " + convertToIndianStandard(thisCountry.active.toString()) + " ↑" + convertToIndianStandard(thisCountry.todayCases.toString())
//            recoveredCasesD.text = "Recovered Cases : " + convertToIndianStandard(thisCountry.recovered.toString())
//            deadCasesD.text =
//                "Deceased : " + convertToIndianStandard(thisCountry.deaths.toString()) + " ↑" + thisCountry.todayDeaths.toString()
//            criticalCasesD.text = "Critical Cases : " + thisCountry.critical
//            TestsD.text = "Total Test : " + convertToIndianStandard(thisCountry.tests.toString())
//            TestsPMD.text = "Tests Per Million : " + convertToIndianStandard(thisCountry.testsPerOneMillion.toString())
//
//            val myFormat = "h:mm a, d MMM YYYY"
//            val sdf = SimpleDateFormat(myFormat)
//            val dateEDT = sdf.format(thisCountry.updated)
//
//            lastUpdtD.text = "Last Updated : $dateEDT"
        }


    }


    private fun convertToIndianStandard(str: String) : String{
        val len = str.length
        return when {
            len>5 -> {
                var ans = str.substring(len-3)
                for (i in len-3 downTo 1 step 2){
                    ans = str.substring((i-2).coerceAtLeast(0),i)+","+ans
                }
                ans
            }
            len>3 -> {
                str.substring(0, len-3)+","+str.substring(len-3)
            }
            else -> {
                str
            }
        }
    }

    fun openDetail(view: View) {
        if(view.nameView.text == "India"){
            startActivity(Intent(this,IndiaActivity::class.java))
        }
        else{
            val i = Intent(this,DetailActivity::class.java)
            i.putExtra("name",view.nameView.text)
            startActivity(i)
            //openDialogDetail(view.nameView.text)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            RC_SETTINGS -> if (sharedPreferences.getBoolean(THEME_CHANGED, false)) {
                recreate()
                sharedPreferencesEditor.putBoolean(THEME_CHANGED, false).commit()
            }
            RC_NETWORK -> if(cd.isConnectingToInternet){
                fetchData()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

