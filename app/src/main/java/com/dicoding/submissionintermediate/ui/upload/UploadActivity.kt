package com.dicoding.submissionintermediate.ui.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.databinding.ActivityUploadBinding
import com.dicoding.submissionintermediate.helper.Utils.getImageUri
import com.dicoding.submissionintermediate.helper.Utils.reduceFileImage
import com.dicoding.submissionintermediate.helper.Utils.uriToFile
import com.dicoding.submissionintermediate.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        uploadViewModel.isLoading.observe(this){
            showLoading(it)
        }

        uploadViewModel.responseUpload.observe(this){ message ->
            val intent = Intent(this@UploadActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            showToast(message)
        }

        uploadViewModel.authToken.observe(this) { authToken ->
            if (authToken.isNullOrEmpty()) {
                val intent = Intent(this@UploadActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.buttonAdd.setOnClickListener {
                    upload()
                }
            }
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.ivPreview.setImageURI(uri)
            updateButtonState()
        }
    }

    private fun setupListeners() {
        binding.edAddDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateButtonState() {
        val isDescNotEmpty = binding.edAddDescription.text.toString().isNotEmpty()
        val isImageSelected = currentImageUri != null
        binding.buttonAdd.isEnabled = isDescNotEmpty && isImageSelected
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun upload(){
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val requestBody = binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            uploadViewModel.uploadImage(multipartBody, requestBody)

        }?: showToast("Pick Photo first")
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading) binding.progressIndicator.visibility = View.VISIBLE else binding.progressIndicator.visibility = View.GONE
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}