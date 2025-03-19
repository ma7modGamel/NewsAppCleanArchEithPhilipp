package com.safwa.newsappcleanarcheithphilipp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article

class ArticlePagingSource(private val source: DataSource) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val articles = source.loadArticles(page, params.loadSize)
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // واجهة لتحديد مصدر البيانات
    interface DataSource {
        suspend fun loadArticles(page: Int, loadSize: Int): List<Article>
    }
}