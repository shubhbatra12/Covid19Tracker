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

    override fun onBindViewHolder(holder: CountryAdapter.UserViewHolder, position: Int) = holder.bind(data[position])

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Country) = with(itemView) {
            nameView.text = item.country
            totalCases.text = item.cases.toString()
            Picasso.get().load(item.countryInfo?.flag).into(imageView)

        }
    }

}