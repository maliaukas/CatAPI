package com.example.catapi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.catapi.network.Cat

class CatViewModelFactory(private val cat: Cat) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatDetailViewModel::class.java)) {
            return CatDetailViewModel(cat) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
