package com.lesforest.vacnacy_test

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class Adapter(val data: MutableList<Article>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(vg: ViewGroup, index: Int): RecyclerView.ViewHolder {
        return ViewHolderArticle(LayoutInflater.from(vg.context), vg)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, index: Int) {
        (vh as ViewHolderArticle).bind(data[index])
    }


    fun addData(list: List<Article>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size


    class ViewHolderArticle(val inflater: LayoutInflater, val parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(R.layout.layout_rv_article, parent, false)
    ) {

        var tvAuthor: TextView
        var tvTitle: TextView
        var tvDesc: TextView
        var tvContent: TextView
        var tvDate: TextView
        var iv: ImageView

        init {
            tvAuthor = itemView.findViewById(R.id.tvAuthor)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDesc = itemView.findViewById(R.id.tvDesc)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvDate = itemView.findViewById(R.id.tvDate)
            iv = itemView.findViewById(R.id.iv)
        }

        fun bind(item: Article) {

            GlideApp.with(parent.context)
                .load(item.urlToImage)
                .into(iv)

            tvAuthor.apply { text = item.author }
            tvContent.apply { text = item.content }
            tvTitle.apply { text = item.title }

//            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(item.publishedAt)


            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val currentDate = sdf.format(date)


            tvDate.apply { text  = currentDate}


            tvDesc.apply { text = item.description }

        }
    }

}
