package com.darkshandev.sutori.presentation.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.darkshandev.sutori.databinding.FragmentStoryListBinding
import com.darkshandev.sutori.presentation.adapter.LoadingStateAdapter
import com.darkshandev.sutori.presentation.adapter.StoryPagedListAdapter
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import kotlinx.coroutines.launch


class StoryListFragment(onItemClicked: StoryPagedListAdapter.Listener) : Fragment() {
    private var binding: FragmentStoryListBinding? = null
    private val storyViewModel by activityViewModels<StoryViewModel>()
    private val adapterM = StoryPagedListAdapter(onItemClicked)

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        storyViewModel.adapterPagedList = adapterM
        val layoutManagerM = LinearLayoutManager(activity)
        binding?.apply {
            rvMain.apply {
                layoutManager = layoutManagerM
                adapter = adapterM.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapterM.retry()
                    }
                )

            }
        }
        initCollector()
        return binding?.root

    }

    private fun initCollector() {
        lifecycleScope.launch {
            storyViewModel.storiesPaged
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    adapterM.submitData(it)
                }
        }
    }


}