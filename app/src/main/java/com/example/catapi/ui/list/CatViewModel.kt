package com.example.catapi.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.catapi.network.Cat
import com.example.catapi.network.CatApi
import com.example.catapi.network.CatApiStatus
import com.example.catapi.ui.CatPagingSource

class CatViewModel : ViewModel(), CatPagingSource.StatusChangedListener {
    val catsFlow = Pager(
        config = PagingConfig(
            pageSize = CATS_PAGE_SIZE
        ),
        pagingSourceFactory = { CatPagingSource(CatApi.retrofitService, this) }
    ).flow.cachedIn(viewModelScope)

    private val _status = MutableLiveData<CatApiStatus>()
    val status: LiveData<CatApiStatus>
        get() = _status

    private val _navigateToSelectedCat = MutableLiveData<Cat?>()
    val navigateToSelectedCat: LiveData<Cat?>
        get() = _navigateToSelectedCat

    fun displayCatDetails(cat: Cat?) {
        _navigateToSelectedCat.value = cat
    }

    fun displayCatDetailsComplete() {
        _navigateToSelectedCat.value = null
    }

    override fun onStatusChanged(status: CatApiStatus) {
        _status.value = status
    }

    companion object {
        const val CATS_PAGE_SIZE = 16
    }
}
