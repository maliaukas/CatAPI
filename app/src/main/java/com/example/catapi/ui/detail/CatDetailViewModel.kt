package com.example.catapi.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.catapi.network.Cat

class CatDetailViewModel(cat: Cat) : ViewModel() {
    private val _selectedCat = MutableLiveData<Cat>()
    val selectedCat: LiveData<Cat>
        get() = _selectedCat

    init {
        _selectedCat.value = cat
    }
}
