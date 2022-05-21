package com.jay.quantumnewsapp.news.data

import com.jay.quantumnewsapp.news.data.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun getTopHeadlines(@Query("country") country: String) : Response<NewsResponse>

    @GET("everything")
    suspend fun searchNewsWithQuery(@Query("q") query: String) : Response<NewsResponse>
}