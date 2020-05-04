package com.shubh.covid19tracker.India

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubh.covid19tracker.*
import kotlinx.android.synthetic.main.activity_india.*
import kotlinx.android.synthetic.main.activity_main.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeThemeInd()
        setContentView(R.layout.activity_india)

        stateRv.apply {
            layoutManager = LinearLayoutManager(this@IndiaActivity)
            adapter = this@IndiaActivity.adapterInd
        }

        fetchDataIndia()
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
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) { ClientStates.api.getMyState() }
            if (response.isSuccessful) {
                response.body()?.let {
                    listInd.clear()
                    originalListInd.addAll(it.state)
                    listInd.addAll(it.state)
                    adapterInd.notifyDataSetChanged()
                }
            }
        }
    }
}
