package com.shubh.covid19tracker


import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.internet_dialog.view.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DARK_THEME = "DarkTheme"
const val RC_SETTINGS = 111
const val THEME_CHANGED = "ThemeChanged"
const val RC_NETWORK = 222

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<Country>()
    val originalList = arrayListOf<Country>()
    val adapter = CountryAdapter(list)

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
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        countryRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        if (cd.isConnectingToInternet) {
            fetchData()
        } else {
            openDialog()
        }

        swipeToRefresh.setOnRefreshListener {
            fetchData()
        }

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

    private fun fetchData() {
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

    private fun openDialog() {

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

    fun openDetail(view: View) {
        val i = Intent(this, DetailActivity::class.java)
        i.putExtra("name", view.nameView.text.toString())
        startActivity(i)
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

