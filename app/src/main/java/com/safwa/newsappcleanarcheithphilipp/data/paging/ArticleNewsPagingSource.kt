package com.safwa.newsappcleanarcheithphilipp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article

class ArticleNewsPagingSource(private val source: DataSource) :PagingSource<Int,Article>() {




    interface DataSource{
        suspend fun loadArticles(page:Int,loadSize:Int) : List<Article>
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        TODO("Not yet implemented")
    }

}