package com.jay.quantumnewsapp.news.data

import com.jay.quantumnewsapp.utils.AUTH_TOKEN
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val baseUrl = "https://newsapi.org/v2/"
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(AuthInterceptor())
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
    }.build()

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val API : NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }
}

// This is used to add the credentials in each request
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", AUTH_TOKEN)
            .build()
        return chain.proceed(request)
    }
}