package com.shubh.covid19tracker.India

data class State(
    val total: Int? = null,
    val death: Int? = null,
    val cured: Int? = null,
    val name: String? = null,
    val id: String? = null,
    val confirmed: Int? = null
) : Comparable<State> {
    override fun compareTo(other: State): Int {
        return if (this.confirmed != null && other.confirmed != null)
            -this.confirmed.compareTo(other.confirmed)
        else 0
    }
}
