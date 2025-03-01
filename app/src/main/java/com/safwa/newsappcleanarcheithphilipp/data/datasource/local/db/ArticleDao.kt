package com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article


@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): LiveData<List<Article>>


    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Int): LiveData<Article>


    @Delete
    suspend fun deleteArticle(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long

//
//    suspend fun updateArticle(article: Article)

}