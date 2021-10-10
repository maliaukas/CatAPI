package com.example.catapi.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.catapi.R
import com.example.catapi.databinding.CatDetailFragmentBinding

class CatDetailFragment : Fragment(R.layout.cat_detail_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = setupBinding(inflater)
        binding.viewModel = setupViewModel()

        binding.setOnClickListeners()

        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater): CatDetailFragmentBinding {
        val binding = CatDetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding
    }

    private fun setupViewModel(): CatDetailViewModel {
        val cat = CatDetailFragmentArgs.fromBundle(requireArguments()).cat
        val viewModelFactory = CatViewModelFactory(cat)
        return ViewModelProvider(this, viewModelFactory).get(CatDetailViewModel::class.java)
    }

    private fun CatDetailFragmentBinding.setOnClickListeners() {
        this.downloadButton.setOnClickListener {
            context?.let { context: Context ->
                viewModel?.saveImage(context)
            }
        }
    }
}
