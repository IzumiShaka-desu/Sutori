package com.darkshandev.sutori.presentation.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.darkshandev.sutori.R
import com.darkshandev.sutori.databinding.FragmentSettingBinding
import com.darkshandev.sutori.presentation.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {
    private val userViewModel by activityViewModels<UserViewModel>()
    private var binding: FragmentSettingBinding? = null
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            userViewModel.sessionUser
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it?.token == null) navController.navigate(R.id.action_settingFragment_to_welcomeFragment)
                }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding?.apply {
            logoutButton.setOnClickListener {
                userViewModel.logout()
            }
            localSetting.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return binding?.root
    }


}