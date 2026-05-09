package com.mkarshnas6.karenstudio.worldskill.data.remote

import android.content.Context
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000"

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val cacheSize = (10 * 1024 * 1024).toLong() // 10MB

//     100 mb
//    100 * 1024 * 1024
//      1 GB
//    1 * 1024 * 1024 * 1024

    private val cache by lazy {
        Cache(File(appContext!!.cacheDir, AppConstant.CacheApi.CACHE_API), cacheSize)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS) // time out connection
            .readTimeout(30, TimeUnit.SECONDS) // time out read
            .writeTimeout(30, TimeUnit.SECONDS) // time out write
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

}