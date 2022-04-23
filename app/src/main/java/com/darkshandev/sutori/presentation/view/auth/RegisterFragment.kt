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
import com.darkshandev.sutori.databinding.FragmentRegisterBinding
import com.darkshandev.sutori.presentation.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null
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
            userViewModel.registerResponse
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is NetworkResult.Initial -> {
                            binding?.apply {
                                progressBarRegister.visibility = View.GONE
                            }
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        is NetworkResult.Error -> {
                            binding?.apply {
                                progressBarRegister.visibility = View.GONE
                            }
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        is NetworkResult.Loading -> {
                            binding?.apply {
                                progressBarRegister.visibility = View.VISIBLE
                            }
                            activity?.window?.setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            )
                        }
                        is NetworkResult.Success -> {
                            binding?.apply {
                                progressBarRegister.visibility = View.GONE
                            }
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            if (it.data != null) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.succes_register),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(R.id.register_navigate_to_login_popped)
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupView()
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
            signupButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                when {
                    !nameEditText.isValidField -> {
                        nameEditTextLayout.error = getString(R.string.msg_invalid_edittext)
                    }
                    !emailEditText.isValidField -> {
                        emailEditTextLayout.error = getString(R.string.msg_invalid_edittext)
                    }
                    !passwordEditText.isValidField -> {
                        passwordEditTextLayout.error = getString(R.string.msg_invalid_edittext)
                    }
                    else -> {
                        userViewModel.registerBy(name, email, password)

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

            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(500)

            val nameEditTextLayout =
                ObjectAnimator.ofFloat(nameEditTextLayout, View.ALPHA, 1f).setDuration(500)

            val emailEditTextLayout =
                ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(500)

            val passwordEditTextLayout =
                ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
            val signup = ObjectAnimator.ofFloat(signupButton, View.ALPHA, 1f).setDuration(500)


            AnimatorSet().apply {
                playSequentially(
                    title,
                    nameEditTextLayout,
                    emailEditTextLayout,
                    passwordEditTextLayout,
                    signup
                )
                startDelay = 500
            }.start()
        }
    }
}