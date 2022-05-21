package com.jay.quantumnewsapp.news.data.models

data class NewsItem(
    val source: NewsSource,
    val title: String,
    val description: String,
    val publishedAt: String,
    val url: String,
    val urlToImage: String,
)
