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
            totalCases.text = convertToIndianStandard(item.total.toString())
            activeCases.text = convertToIndianStandard(item.confirmed.toString())
            deadCases.text = convertToIndianStandard(item.death.toString())
            recoveredCases.text = convertToIndianStandard(item.cured.toString())
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