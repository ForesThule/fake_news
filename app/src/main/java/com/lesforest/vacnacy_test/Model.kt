package com.lesforest.vacnacy_test

data class ResponseModel(val status: String,val totalResults: Int, val articles: MutableList<Article> )

data class StorageModel(
    var totalResults: Int,
    val articles: MutableList<Article> = emptyList<Article>().toMutableList() ){

    companion object{
        fun getEmpty(): StorageModel = StorageModel(0, emptyList<Article>().toMutableList())
    }

    fun canLoadMore(): Boolean =  if (totalResults==0){true}else{articles.size<totalResults}


    fun nextPage(): Int {

        val i = totalResults % 20
        val ii = totalResults / 20
        val totalPages =  (if (i == 0) ii else ii + 1)
        val x = articles.size % 20
        val xx = articles.size / 20
        val storedPages =  (if (x == 0) xx else xx + 1)

        return if (storedPages<totalPages)storedPages+1 else if (storedPages==0)1 else storedPages
    }
}



data class Source(val id: String, val name: String)

data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String)


