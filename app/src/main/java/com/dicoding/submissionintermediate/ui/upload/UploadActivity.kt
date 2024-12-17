package com.dicoding.submissionintermediate.ui.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.databinding.ActivityUploadBinding
import com.dicoding.submissionintermediate.helper.Utils.getImageUri
import com.dicoding.submissionintermediate.helper.Utils.reduceFileImage
import com.dicoding.submissionintermediate.helper.Utils.uriToFile
import com.dicoding.submissionintermediate.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        uploadViewModel.isLoading.observe(this){
            showLoading(it)
            showButton(it)
        }

        uploadViewModel.successUpload.observe(this){ message ->
            moveActivity()
            showToast(message)
        }

        uploadViewModel.failUpload.observe(this){ message ->
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
        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            }
        }

    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Toast.makeText(
                        this,
                        "Lokasi aktif!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation()
            } else {
                Toast.makeText(
                    this,
                    "Izin lokasi tidak diberikan.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        launcherGallery.launch(intent)
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                currentImageUri = uri
                showImage()
            } else {
                showToast("No media selected")
            }
        } else {
            showToast("Action canceled")
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

    @SuppressLint("MissingPermission")
    private fun upload() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                val lon = location?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())

                uploadViewModel.uploadImage(multipartBody, description, lat, lon)
            }.addOnFailureListener {
                uploadViewModel.uploadImage(multipartBody, description)
            }
        } ?: showToast("Pick Photo first")
    }


    private fun showLoading(isLoading: Boolean){
        if(isLoading) binding.progressIndicator.visibility = View.VISIBLE else binding.progressIndicator.visibility = View.GONE
    }

    private fun showButton(isEnabled: Boolean){
        binding.buttonAdd.isEnabled = !isEnabled
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun moveActivity(){
        val intent = Intent(this@UploadActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}