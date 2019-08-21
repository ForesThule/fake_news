package com.lesforest.vacnacy_test

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.DividerItemDecoration


val APP_PREFERENCES = "mysettings"
val PREFS_ARTICLES = "ARTICLES"



class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var pref: SharedPreferences
    lateinit var api: Api

    private fun getArticlesData(): StorageModel {
        val articlesString = pref.getString(PREFS_ARTICLES, "")
        return Gson().fromJson(articlesString, StorageModel::class.java)?:StorageModel.getEmpty()
    }

    fun saveArticlesData(articlesList: ResponseModel){

        val storageModel = getArticlesData()

        storageModel.let {
            val editor = pref.edit()

            it.totalResults= articlesList.totalResults
            it.articles.addAll(articlesList.articles)

            editor.putString(PREFS_ARTICLES, Gson().toJson(it))
            editor.apply()

        }
    }

    lateinit var swipe: SwipeRefreshLayout

    @SuppressLint("CheckResult")
    override fun onRefresh() {
        swipe.isRefreshing = true
        getMoreArticles()
    }

    private fun getMoreArticles() {

        if(isNetworkAvailable(context = this)){
            if (canLoadMore()){
                fetchArticles()
            }else{
                swipe.isRefreshing = false
                Toast.makeText(this,"Статей больше нет",Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(this,"Подключение к сети отсутствует",Toast.LENGTH_LONG).show()

        }




    }

    private fun canLoadMore(): Boolean {

        val b = getArticlesData().canLoadMore()

        println("CAN WE LOAD MORE? $b")

        return b
    }

    @SuppressLint("CheckResult")
    private fun fetchArticles() {
        val nextPage = getArticlesData().nextPage()
        api.getArticles(page = nextPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ r: ResponseModel? ->

                stub_tv.visibility = View.GONE

                r?.let {
                    saveArticlesData(it)
                    artAdapter.addData(it.articles)
                }
                swipe.isRefreshing = false

            },
                { t: Throwable? -> t?.printStackTrace() })
    }

    lateinit var artAdapter: Adapter

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        artAdapter = Adapter(getArticlesData().articles)
        api = Api.invoke()

        if (getArticlesData().articles.isNullOrEmpty()){
            stub_tv.visibility = View.VISIBLE
        }

        rv?.apply {
            adapter = artAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, true)
            addItemDecoration(DividerItemDecoration(
                context,
                RecyclerView.VERTICAL
            ))


            swipe_container?.let {
                swipe = it
                swipe.setOnRefreshListener (this@MainActivity)
                swipe.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark)
            }
        }
    }
}
