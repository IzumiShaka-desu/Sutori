package com.darkshandev.sutori.presentation.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.databinding.FragmentHomeBinding
import com.darkshandev.sutori.presentation.adapter.MainListAdapter
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), MainListAdapter.Listener {
    private var binding: FragmentHomeBinding? = null
    private val storyViewModel by activityViewModels<StoryViewModel>()
    private val adapterM by lazy { MainListAdapter(this) }
    private val navController: NavController by lazy {
        Navigation.findNavController(activity = activity as Activity, R.id.fragmentContainerView)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()
        initCollector()
        return binding?.root
    }

    private fun initView() {
        val layoutManagerM = LinearLayoutManager(activity)

        binding?.apply {
            NavigationUI.setupWithNavController(
                toolbar,
                navController,
                AppBarConfiguration(setOf(R.id.homeFragment))
            )
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_setting -> {
                        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }
                    else -> {
                        false

                    }
                }
            }
            fabHome.setOnClickListener {
                navController.navigate(R.id.navigateToCreatePost)
            }
            rvMain.apply {
                layoutManager = layoutManagerM
                adapter = adapterM
            }
        }
    }

    private fun initCollector() {
        lifecycleScope.launch {
            storyViewModel.stories
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is NetworkResult.Initial -> {
                            binding?.apply {
                                errorMessage.visibility = View.GONE
                                rvMain.visibility = View.GONE
                                progressBarHome.visibility = View.GONE
                            }
                            storyViewModel.fetchStories()
                        }
                        is NetworkResult.Loading -> {
                            binding?.apply {

                                errorMessage.visibility = View.GONE
                                rvMain.visibility = View.GONE
                                progressBarHome.visibility = View.VISIBLE
                            }
                        }
                        is NetworkResult.Error -> {
                            binding?.apply {
                                errorMessage.text = it.message
                                errorMessage.visibility = View.VISIBLE
                                rvMain.visibility = View.GONE
                                progressBarHome.visibility = View.GONE
                            }
                        }
                        is NetworkResult.Success -> {
                            binding?.apply {
                                it.data?.let { it1 -> adapterM.updateList(it1.listStory) }
                                errorMessage.visibility = View.GONE
                                rvMain.visibility = View.VISIBLE
                                progressBarHome.visibility = View.GONE

                            }
                        }
                    }
                }
        }
    }

    override fun onItemClickListener(view: View, story: Story, sharedView: View?) {
        storyViewModel.setSelectedPost(story)
        if (sharedView != null) {
            val extras = FragmentNavigatorExtras(sharedView to "full_image")
            navController.navigate(R.id.home_navigate_to_detail, null, null, extras)
        } else {
            navController.navigate(R.id.home_navigate_to_detail)

        }

    }


}