package com.darkshandev.sutori.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.darkshandev.sutori.R
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.databinding.FragmentCreatePostBinding
import com.darkshandev.sutori.presentation.viewmodels.StoryViewModel
import com.darkshandev.sutori.utils.createFile
import com.darkshandev.sutori.utils.createTempFile
import com.darkshandev.sutori.utils.uriToFile
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CreatePostFragment : Fragment() {
    private var binding: FragmentCreatePostBinding? = null
    private val storyViewModel: StoryViewModel by activityViewModels()
    private var cameraExecutor: ExecutorService? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    override fun onDestroyView() {
        binding = null
        cameraExecutor?.shutdown()
        cameraExecutor = null
        storyViewModel.resetForm()
        storyViewModel.setLocationUpdateListenTo(false)
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        initView()
        initCollector()
        return binding?.root
    }

    private fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding?.apply {
            closeButton.setOnClickListener {
                storyViewModel.resetForm()
                descriptionText.setText("")
            }
            descriptionText.addTextChangedListener {
                storyViewModel.setDescription(descriptionText.text.toString())
            }
            switch1.setOnCheckedChangeListener { _, isActive ->
                storyViewModel.setLocationUpdateListenTo(isActive)
            }
            externalCameraButton.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.resolveActivity(requireActivity().packageManager)

                createTempFile(requireActivity().application).also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.darkshandev.sutori",
                        it
                    )
                    currentPhotoPath = it.absolutePath
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    launcherIntentCamera.launch(intent)
                }
            }
            selectFromGallery.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, getString(R.string.pick_photo))
                launcherIntentGallery.launch(chooser)
            }
            captureImage.setOnClickListener { takePhoto() }
            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                startCamera()
            }
        }
    }

    private var currentPhotoPath: String? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            storyViewModel.setImage(myFile)
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, requireContext())

            storyViewModel.setImage(myFile)

        }
    }

    override fun onResume() {
        super.onResume()
        if (storyViewModel.image.value == null) {
            startCamera()
        }
    }

    private fun startCamera() {

        binding?.apply {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(preview.surfaceProvider)
                    }
                imageCapture = ImageCapture.Builder().build()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_get_camera),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, ContextCompat.getMainExecutor(requireContext()))

        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(requireActivity().application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_pick_photo),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri
                    storyViewModel.setImage(photoFile)
                }
            }
        )

    }

    private fun initCollector() {
        lifecycleScope.launch {
            storyViewModel.locationUpdates
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    storyViewModel.setLocation(it?.latitude, it?.longitude)
                }
        }
        lifecycleScope.launch {
            storyViewModel.userRequest.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it.description.isNotBlank() && binding!!.descriptionText.text?.isNotBlank() == true) {
                        binding?.apply {
                            sendButton.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity().applicationContext,
                                    R.color.primaryDarkColor
                                )
                            )
                            sendButton.setOnClickListener {
                                storyViewModel.postImage()
                            }
                        }
                    } else {
                        binding?.apply {
                            sendButton.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity().applicationContext,
                                    R.color.grey
                                )
                            )
                            sendButton.setOnClickListener {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.please_add_description),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        }
        lifecycleScope.launch {
            storyViewModel.postResponse
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is NetworkResult.Initial -> {
                            binding?.apply {
                                progressBar.visibility = View.GONE
                            }
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                        is NetworkResult.Loading -> {
                            binding?.apply {
                                progressBar.visibility = View.VISIBLE
                            }
                            activity?.window?.setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            );
                        }
                        is NetworkResult.Success -> {
                            binding?.apply {
                                progressBar.visibility = View.GONE
                            }
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.succes_upload),
                                Toast.LENGTH_SHORT
                            ).show()
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            activity?.onBackPressed()
                        }
                        is NetworkResult.Error -> {
                            binding?.apply {
                                progressBar.visibility = View.GONE
                            }
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }
                }
        }
        lifecycleScope.launch {
            storyViewModel.image
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    if (it != null) {
                        cameraExecutor?.shutdown()
                        binding?.apply {
                            previewPost.visibility = View.VISIBLE
                            takePhotoView.visibility = View.GONE
                            Glide.with(this@CreatePostFragment)
                                .load(it)
                                .centerCrop()
                                .into(previewImage)
                        }
                    } else {
                        cameraExecutor = null
                        cameraExecutor = Executors.newSingleThreadExecutor()
                        startCamera()
                        binding?.apply {
                            previewPost.visibility = View.GONE
                            takePhotoView.visibility = View.VISIBLE
                            Glide.with(this@CreatePostFragment)
                                .load(R.drawable.ic_baseline_broken_image_24)
                                .into(previewImage)
                        }
                    }
                }
        }
    }

}