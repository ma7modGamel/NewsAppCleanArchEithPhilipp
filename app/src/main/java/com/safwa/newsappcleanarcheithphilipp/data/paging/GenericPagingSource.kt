package com.safwa.newsappcleanarcheithphilipp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GenericPagingSource<T : Any>(private val dataSource: DataSource<T>) : PagingSource<Int, T>() {

    interface DataSource<T> {
        // page :  رقم الصفحة اللي عايز تجيبها (يبدأ من 1).
        //
        //loadSize: عدد العناصر في الصفحة (بتتحدد من PagingConfig).
        suspend fun loadItems(page: Int, loadSize: Int): List<T>

    }

//
//    بتحدد نقطة البداية لإعادة تحميل البيانات (Refresh)
//    لو حصل تغيير في الـ Data (مثل إضافة/حذف عنصر أو تحديث).
//    بيحتوي على معلومات عن الصفحات المحملة حاليًا:  PagingState
 //   امتى بيشتغل؟: بيتم استدعاؤه لما تعمل adapter.refresh()
    //   أو لما Paging 3 تحتاج تعيد تحميل البيانات من نقطة معينة (مثل بعد تغيير في الـ Data Source).


    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = dataSource.loadItems(nextPageNumber, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = if (response.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}