package com.jay.quantumnewsapp.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jay.quantumnewsapp.R
import com.jay.quantumnewsapp.databinding.NewsItemBinding
import com.jay.quantumnewsapp.news.data.models.NewsItem
import com.jay.quantumnewsapp.utils.dateToTimeFormat

class NewsAdapter(private val onNewsClicked: (NewsItem) -> Unit) :
    ListAdapter<NewsItem, NewsAdapter.NewsViewHolder>(DiffCallback) {

    class NewsViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsItem) {
            binding.apply {
                newsTsSource.text = newsTsSource.context
                    .getString(R.string.ts_source_template, dateToTimeFormat(news.publishedAt), news.source.name)
                newsTitle.text = news.title
                newsDesc.text = news.description
                Glide.with(binding.newsImage).load(news.urlToImage)
                    .error(R.drawable.ic_error).into(binding.newsImage)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<NewsItem>(){
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val viewHolder = NewsViewHolder(
            NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        viewHolder.itemView.setOnClickListener {
            onNewsClicked(getItem(viewHolder.absoluteAdapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}