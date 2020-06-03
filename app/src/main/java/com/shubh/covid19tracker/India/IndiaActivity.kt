package com.shubh.covid19tracker.India

import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.shubh.covid19tracker.*
import kotlinx.android.synthetic.main.activity_india.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.internet_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IndiaActivity : AppCompatActivity() {

    val listInd = arrayListOf<State>()
    val originalListInd = arrayListOf<State>()
    val adapterInd = StateAdapter(listInd)

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val sharedPreferencesEditor by lazy {
        sharedPreferences.edit()
    }
    private val cdIn by lazy { ConnectionDetector(this) }

    private val mAppUnitId: String by lazy {
        "ca-app-pub-5689524874061492~2817334170"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeThemeInd()
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_india)

        stateRv.apply {
            layoutManager = LinearLayoutManager(this@IndiaActivity)
            adapter = this@IndiaActivity.adapterInd
        }

        fetchDataIndia()

        swipeToRefreshInd.setOnRefreshListener {
            fetchDataIndia()
        }

        fabIND.setOnClickListener{
            finish()
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
        adViewInd.loadAd(adRequest)
    }

    private fun changeThemeInd() {
        if (sharedPreferences.getBoolean(DARK_THEME, true)) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            RC_SETTINGS -> if (sharedPreferences.getBoolean(THEME_CHANGED, false)) {
                recreate()
                sharedPreferencesEditor.putBoolean(THEME_CHANGED, false).commit()
            }
            RC_NETWORK -> if(cdIn.isConnectingToInternet){
                fetchDataIndia()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fetchDataIndia() {
        if (cdIn.isConnectingToInternet) {
            GlobalScope.launch(Dispatchers.Main) {
                swipeToRefreshInd.isRefreshing = true
                val response = withContext(Dispatchers.IO) { ClientStates.api.getMyState() }
                if (response.isSuccessful) {
                    response.body()?.let {
                        listInd.clear()
                        originalListInd.addAll(it.state)
                        listInd.addAll(it.state)

                        //Sorting
                        originalListInd.sort()
                        listInd.sort()

                        adapterInd.notifyDataSetChanged()
                    }
                }
                swipeToRefreshInd.isRefreshing = false
            }

        } else {
            openDialogInternetInd()
            swipeToRefreshInd.isRefreshing = false
        }
    }

    private fun openDialogInternetInd() {

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
