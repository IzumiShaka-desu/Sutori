package com.darkshandev.sutori.presentation.view.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.databinding.FragmentHomeBinding
import com.darkshandev.sutori.presentation.adapter.PagerAdapter
import com.darkshandev.sutori.presentation.adapter.StoryPagedListAdapter
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.internal.immutableListOf


class HomeFragment : Fragment(), StoryPagedListAdapter.Listener {
    private var binding: FragmentHomeBinding? = null
    private val storyViewModel by activityViewModels<StoryViewModel>()
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
        return binding?.root
    }

    private fun initView() {
        binding?.apply {
            NavigationUI.setupWithNavController(
                toolbar,
                navController,
                AppBarConfiguration(setOf(R.id.homeFragment))
            )
            val tabAdapter = PagerAdapter(
                immutableListOf(StoryListFragment(this@HomeFragment), MapsFragment()),
                childFragmentManager,
                lifecycle
            )
            homeVp.adapter = tabAdapter
            homeVp.isUserInputEnabled = false
            TabLayoutMediator(tabHome, homeVp) { tab, position ->
                when (position) {
                    0 -> tab.setIcon(R.drawable.ic_baseline_home_24)
                    1 -> tab.setIcon(R.drawable.ic_baseline_map_24)
                }
            }.attach()
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