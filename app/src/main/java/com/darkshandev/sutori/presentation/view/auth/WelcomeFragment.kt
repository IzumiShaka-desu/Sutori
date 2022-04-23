package com.darkshandev.sutori.presentation.view.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.isExpire
import com.darkshandev.sutori.databinding.FragmentWelcomeBinding
import com.darkshandev.sutori.presentation.viewmodels.UserViewModel
import com.darkshandev.sutori.utils.JWTUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {
    private var binding: FragmentWelcomeBinding? = null
    private val userViewModel by activityViewModels<UserViewModel>()


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            userViewModel.sessionUser
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it?.token != null) {
                        if (JWTUtils.decoded(it.token)?.isExpire() == true) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.session_expire),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            navController.navigate(R.id.welcome_to_home)
                        }
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setupView()
        setupAction()
        playAnimation()
        return binding?.root

    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    private fun setupAction() {
        binding?.loginButton?.setOnClickListener {
            navController.navigate(R.id.welcome_navigate_to_login)
        }

        binding?.signupButton?.setOnClickListener {
            navController.navigate(R.id.welcome_navigate_to_register)
        }
    }

    private fun playAnimation() {
        binding?.apply {

            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(500)
            val signup =
                ObjectAnimator.ofFloat(signupButton, View.ALPHA, 1f).setDuration(500)
            val title =
                ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(500)
            val desc = ObjectAnimator.ofFloat(descTextView, View.ALPHA, 1f).setDuration(500)

            val together = AnimatorSet().apply {
                playTogether(login, signup)
            }

            AnimatorSet().apply {
                playSequentially(title, desc, together)
                start()
            }
        }
    }
}