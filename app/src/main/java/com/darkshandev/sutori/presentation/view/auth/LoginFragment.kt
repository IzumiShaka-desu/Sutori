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
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.isExpire
import com.darkshandev.sutori.databinding.FragmentLoginBinding
import com.darkshandev.sutori.presentation.viewmodels.UserViewModel
import com.darkshandev.sutori.utils.JWTUtils
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
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
                        if (JWTUtils.decoded(it.token)
                                ?.isExpire() != true
                        ) navController.navigate(R.id.navigate_login_to_home)

                    }
                }
        }
        lifecycleScope.launch {
            userViewModel.loginResponse
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is NetworkResult.Initial -> {
                            binding?.apply {
                                progressBarLogin.visibility = View.GONE
                            }
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        is NetworkResult.Error -> {
                            binding?.apply {
                                progressBarLogin.visibility = View.GONE
                            }
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        is NetworkResult.Loading -> {
                            binding?.apply {
                                progressBarLogin.visibility = View.VISIBLE
                            }
                            activity?.window?.setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            )
                        }
                        is NetworkResult.Success -> {
                            binding?.apply {
                                progressBarLogin.visibility = View.GONE
                            }
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            if (it.data != null) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.succes_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupView()
//        setupViewModel()
        setupAction()
        playAnimation()
        return binding?.root
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
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
        binding?.apply {
            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                when {
                    !emailEditText.isValidField -> {
                        emailEditTextLayout.error = getString(R.string.msg_invalid_edittext)
                    }
                    !passwordEditText.isValidField -> {
                        passwordEditTextLayout.error = getString(R.string.msg_invalid_edittext)
                    }

                    else -> {
                        userViewModel.loginBy(email, password)
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        binding?.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title =
                ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(500)
            val message =
                ObjectAnimator.ofFloat(messageTextView, View.ALPHA, 1f).setDuration(500)

            val emailEditTextLayout =
                ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(500)

            val passwordEditTextLayout =
                ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f)
                    .setDuration(500)
            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(500)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    message,
                    emailEditTextLayout,
                    passwordEditTextLayout,
                    login
                )
                startDelay = 500
            }.start()
        }
    }

}