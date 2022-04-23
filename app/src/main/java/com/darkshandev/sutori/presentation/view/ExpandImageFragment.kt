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
import com.darkshandev.sutori.data.models.formatDate
import com.darkshandev.sutori.databinding.FragmentExpandImageBinding
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import kotlinx.coroutines.launch


class ExpandImageFragment : Fragment() {
    private val storyViewModel by activityViewModels<StoryViewModel>()
    private var binding: FragmentExpandImageBinding? = null
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.slide_bottom)
        sharedElementEnterTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpandImageBinding.inflate(inflater, container, false)
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