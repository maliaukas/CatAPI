package com.example.catapi.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.catapi.R
import com.example.catapi.databinding.CatListFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatsListFragment : Fragment() {

    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = CatListFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = CatGridAdapter(
            CatGridAdapter.OnClickListener {
                viewModel.displayCatDetails(it)
            }
        )

        val marginSize = resources.getDimension(R.dimen.padding).toInt()

        binding.list.addItemDecoration(CatGridAdapter.MarginItemDecoration(marginSize))
        binding.list.adapter = adapter

        lifecycleScope.launch {
            viewModel.catsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewModel.navigateToSelectedCat.observe(viewLifecycleOwner, {
            if (it != null) {
                this
                    .findNavController()
                    .navigate(
                        CatsListFragmentDirections
                            .actionCatsListFragmentToCatDetailFragment(it),
                        navOptions
                    )
                viewModel.displayCatDetailsComplete()
            }
        })

        return binding.root
    }

    companion object {
        val navOptions by lazy {
            NavOptions.Builder()
                .setEnterAnim(R.animator.card_flip_right_in)
                .setExitAnim(R.animator.card_flip_right_out)
                .setPopEnterAnim(R.animator.card_flip_left_in)
                .setPopExitAnim(R.animator.card_flip_left_out)
                .build()
        }
    }
}
