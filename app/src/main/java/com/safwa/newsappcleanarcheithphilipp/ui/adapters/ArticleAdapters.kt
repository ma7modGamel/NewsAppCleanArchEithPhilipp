package com.safwa.newsappcleanarcheithphilipp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel.Article
import com.safwa.newsappcleanarcheithphilipp.databinding.ItemArticleBinding

class ArticleAdapters() : ListAdapter<Article, ArticleAdapters.ArticleViewHolder>(ArticleDiffUtils()) {


    class ArticleViewHolder(private val binding: ItemArticleBinding) : ViewHolder(binding.root) {

        fun bind(item: Article?) {
            binding.tv.text = item?.title
            binding.tv2.text = item?.description
            Glide.with(binding.root.context)
                .load(item?.urlToImage)
                .into(binding.img)
        }
    }

    class ArticleDiffUtils :DiffUtil.ItemCallback<Article>(){

            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id == newItem.id // قارن باستخدام معرف فريد
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem // قارن المحتوى الكلي
            }

        }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ArticleViewHolder {

//        ItemArticleBinding.inflate(p0.context.getSystemService(LayoutInflater::class.java),p0,false)
        val articleBinding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(articleBinding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, p1: Int) {
        holder.bind(getItem(p1))
    }
}











