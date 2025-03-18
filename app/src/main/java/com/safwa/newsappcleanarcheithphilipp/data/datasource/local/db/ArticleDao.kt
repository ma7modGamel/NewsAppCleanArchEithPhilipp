package com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel.Article


@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>


    @Query("SELECT * FROM articles WHERE id = :id")
     fun getArticleById(id: Int): LiveData<Article>


    @Delete
    fun deleteArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun upsert(article: Article) : Long

//
//    suspend fun updateArticle(article: Article)

}