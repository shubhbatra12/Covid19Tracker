package com.shubh.covid19tracker


data class SearchResponse(
    val totalCount: Int? = null,
    val incompleteResults: Boolean? = null,
    val items: List<Country>
)