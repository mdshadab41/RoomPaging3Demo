package com.example.roompaging3demo.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.roompaging3demo.db.ItemDao
import com.example.roompaging3demo.model.Item
import kotlinx.coroutines.delay

class MainPagingSource(
    private val dao: ItemDao
): PagingSource<Int, Item>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val page = params.key ?: 0

        return try {
            val entites = dao.getPagedList(params.loadSize,page*params.loadSize)

            //simulate page loading
            if (page!=0) delay(100)

            LoadResult.Page(
                data = entites,
                prevKey = if (page==0) null else page-1,
                nextKey = if(entites.isEmpty()) null else page+1
            )
        }catch(e:Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}