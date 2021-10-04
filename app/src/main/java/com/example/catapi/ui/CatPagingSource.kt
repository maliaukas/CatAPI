package com.example.catapi.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.catapi.network.Cat
import com.example.catapi.network.CatApiService
import com.example.catapi.network.CatApiStatus
import com.example.catapi.ui.list.CatViewModel.Companion.CATS_PAGE_SIZE
import java.io.IOException
import retrofit2.HttpException

class CatPagingSource(
    private val catApiService: CatApiService,
    private val statusChangedListener: StatusChangedListener
) :
    PagingSource<Int, Cat>() {

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        val position = params.key ?: CATS_STARTING_PAGE_INDEX

        statusChangedListener.onStatusChanged(CatApiStatus.LOADING)

        return try {
            val cats = catApiService.getCats(params.loadSize, position)
            val nextKey = if (cats.isEmpty()) {
                null
            } else {
                position + (params.loadSize / CATS_PAGE_SIZE)
            }

            val result = LoadResult.Page(
                data = cats,
                prevKey = if (position == CATS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )

            statusChangedListener.onStatusChanged(CatApiStatus.DONE)

            result
        } catch (exception: IOException) {
            statusChangedListener.onStatusChanged(CatApiStatus.ERROR)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            statusChangedListener.onStatusChanged(CatApiStatus.ERROR)
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val CATS_STARTING_PAGE_INDEX = 0
    }

    interface StatusChangedListener {
        fun onStatusChanged(status: CatApiStatus)
    }
}
