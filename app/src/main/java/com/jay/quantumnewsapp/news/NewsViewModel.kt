package com.jay.quantumnewsapp.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.quantumnewsapp.news.data.RetrofitInstance
import com.jay.quantumnewsapp.news.data.models.NewsItem
import com.jay.quantumnewsapp.news.data.models.NewsResponse
import com.jay.quantumnewsapp.utils.TAG
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class NewsViewModel: ViewModel() {

    var topHeadlines : List<NewsItem> = emptyList()

    fun getTopHeadlines(resultCallback: (List<NewsItem>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response : Response<NewsResponse> = RetrofitInstance.API.getTopHeadlines("in")
                if(response.isSuccessful) {
                    val articles = response.body()?.articles ?: throw Exception("No articles found in response!")
                    resultCallback(articles)
                    topHeadlines = articles
                } else throw Exception(response.errorBody().toString())
            } catch (exception: Exception) {
                Log.d(TAG, "topHeadlines: $exception")
                resultCallback(null)
            }
        }
    }

    fun searchNews(query: String, resultCallback: (List<NewsItem>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response : Response<NewsResponse> = RetrofitInstance.API.searchNewsWithQuery(query)
                if(response.isSuccessful) {
                    val articles = response.body()?.articles ?: throw Exception("No articles found in response!")
                    resultCallback(articles)
                } else throw Exception(response.errorBody().toString())
            } catch (exception: Exception) {
                Log.d(TAG, "searchNews: $exception")
                resultCallback(null)
            }
        }
    }
}