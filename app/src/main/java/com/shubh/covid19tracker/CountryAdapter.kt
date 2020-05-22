package com.shubh.covid19tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_country.view.*

class CountryAdapter(val data: List<Country>) : RecyclerView.Adapter<CountryAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_country, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.bind(data[position])

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Country) = with(itemView) {
            nameView.text = item.country
            totalCases.text = convertToIndianStandard(item.cases.toString())
            activeCases.text = convertToIndianStandard(item.active.toString())
            deadCases.text = convertToIndianStandard(item.deaths.toString())
            recoveredCases.text = convertToIndianStandard(item.recovered.toString())
            Picasso.get().load(item.countryInfo?.flag).into(imageView)

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
    }


}