package com.shubh.covid19tracker.India

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubh.covid19tracker.R
import kotlinx.android.synthetic.main.item_state.view.*

class StateAdapter(val data: List<State>) : RecyclerView.Adapter<StateAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_state, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.bind(data[position])

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: State) = with(itemView) {
            nameView.text = item.name
            totalCases.text = item.total.toString()
            activeCases.text = item.confirmed.toString()
            deadCases.text = item.death.toString()
            recoveredCases.text =item.cured.toString()
        }
    }

}