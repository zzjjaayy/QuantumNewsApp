package com.jay.quantumnewsapp.news.data.models

data class NewsResponse(
    val status: String,
    val articles: List<NewsItem>
)
