package com.darkshandev.sutori.presentation.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.databinding.FragmentMapsBinding
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {
    private var binding: FragmentMapsBinding? = null
    private val storyViewModel by activityViewModels<StoryViewModel>()
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private val callback = OnMapReadyCallback { googleMap ->

        lifecycleScope.launch {
            storyViewModel.storiesOnMap
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { stories ->

                    googleMap.clear()
                    if (stories is NetworkResult.Success) {
                        stories.data?.listStory?.forEach {
                            val location = LatLng(it.lat!!, it.lon!!)
                            googleMap.addMarker(MarkerOptions().position(location).title(it.name))
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(location))
                        }

                    }
                    if (stories is NetworkResult.Error) {
                        Toast.makeText(requireContext(), stories.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}