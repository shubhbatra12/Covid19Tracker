package com.shubh.covid19tracker.India

data class SearchResponse(
    val totalCount: Int? = null,
    val incompleteResults: Boolean? = null,
    val state: List<State>
)