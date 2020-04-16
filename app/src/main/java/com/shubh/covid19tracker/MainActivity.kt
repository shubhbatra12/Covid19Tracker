package com.shubh.covid19tracker


import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
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


        countryRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchUsers(it) }
                return true
            }

        })
        searchView.setOnCloseListener {
            fetchData()
            return@setOnCloseListener true
        }

        fetchData()
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) { Client.api.getMyUser() }
            if (response.isSuccessful) {
                response.body()?.let {
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
            list.clear()
            fetchData()
        }
    }
}

