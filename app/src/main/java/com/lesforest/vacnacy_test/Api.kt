package com.lesforest.vacnacy_test

import io.reactivex.Single
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface Api {

    @GET("v2/top-headlines?pageSize=20&country=us&apiKey=d778aa33f80746fa9b388e6880dbf363")
    fun getArticles(@Query("page") page: Int): Single<ResponseModel>



    companion object {
        // redefine invoke() to create Api instance
        operator fun invoke(): Api {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val jar = object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host()] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = cookieStore[url.host()]
                    return cookies ?: ArrayList()
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
//                    .addInterceptor(BasicAuthInterceptor(TEST_UID, TEST_PWD))
                .cookieJar(jar)

            okHttpClient.addNetworkInterceptor(loggingInterceptor)

//      OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            okHttpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }



            return Retrofit.Builder()
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://newsapi.org/")
                .build()
                .create(Api::class.java)
        }
    }
}