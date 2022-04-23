package com.darkshandev.sutori.presentation.view

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.formatDate
import com.darkshandev.sutori.databinding.FragmentDetailBinding
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private val storyViewModel by activityViewModels<StoryViewModel>()
    private var binding: FragmentDetailBinding? = null
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_bottom)
        sharedElementEnterTransition = animation
        returnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding?.apply {
            imageDetailView.setOnClickListener {
                val extras = FragmentNavigatorExtras(it to "expand_image")
                navController.navigate(
                    R.id.action_detailFragment_to_expandImageFragment,
                    null,
                    null,
                    extras
                )
            }
        }
        lifecycleScope.launch {
            storyViewModel.selectedStory
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it != null) {
                        binding?.apply {
                            story = it
                            date = it.formatDate()

                        }
                    }
                }
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        startPostponedEnterTransition()

    }

}