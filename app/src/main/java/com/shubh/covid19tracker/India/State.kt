package com.shubh.covid19tracker.India

data class State(
    val total: Int? = null,
    val death: Int? = null,
    val cured: Int? = null,
    val name: String? = null,
    val id: String? = null,
    val active: Int? = null
) : Comparable<State> {
    override fun compareTo(other: State): Int {
        return if (this.active != null && other.active != null)
            -this.active.compareTo(other.active)
        else 0
    }
}
