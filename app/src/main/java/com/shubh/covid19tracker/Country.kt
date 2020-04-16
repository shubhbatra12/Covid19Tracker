package com.shubh.covid19tracker

data class Country(
	val country: String? = null,
	val cases: Int? = null,
	val critical: Int? = null,
	val active: Int? = null,
	val testsPerOneMillion: Int? = null,
	val recovered: Int? = null,
	val tests: Int? = null,
	val deathsPerOneMillion: Int? = null,
	val casesPerOneMillion: Int? = null,
	val countryInfo: CountryInfo? = null,
	val updated: Long? = null,
	val deaths: Int? = null,
	val todayCases: Int? = null,
	val todayDeaths: Int? = null
)
