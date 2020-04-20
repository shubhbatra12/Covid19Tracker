package com.shubh.covid19tracker


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    val list = arrayListOf<Country>()
    val originalList = arrayListOf<Country>()
    val adapter = CountryAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        countryRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                fetchData()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                fetchData()
                return true
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    searchUsers(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) { Client.api.getMyUser() }
            if (response.isSuccessful) {
                response.body()?.let {
                    list.clear()
                    originalList.addAll(it)
                    list.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun searchUsers(query: String) {
        if (query.length>=2) {
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
        }
        else{
            fetchData()
        }
    }

    fun openDetail(view : View){
        val i = Intent(this,DetailActivity::class.java)
        i.putExtra("name",view.nameView.text.toString())
        startActivity(i)
    }
}

