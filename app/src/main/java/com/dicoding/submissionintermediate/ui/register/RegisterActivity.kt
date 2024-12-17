package com.dicoding.submissionintermediate.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.submissionintermediate.databinding.ActivityRegisterBinding
import com.dicoding.submissionintermediate.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        playAnimation()
        setupRegisterObserver()

        binding.tvRedirect.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()

        }

    }


    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            registerViewModel.registerUser(name, email, password)

            registerViewModel.isSuccess.observe(this) { response ->
                showSuccessDialog(response.message!!)
            }
        }
    }


    private fun moveToLogin(){
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()

    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Yeay!")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                moveToLogin()
            }
            .setCancelable(false)
            .show()
    }



    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Oops!")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showButton(isEnabled: Boolean){
        binding.btnRegister.isEnabled = !isEnabled
    }

    private fun setupRegisterObserver(){
        registerViewModel.isLoading.observe(this){
            showLoading(it)
            showButton(it)
        }

        registerViewModel.responseRegister.observe(this){
            showErrorDialog(it)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(200)
        val etName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val tvEmail =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val etEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val etPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(binding.llLogin, View.ALPHA, 1f).setDuration(200)


        AnimatorSet().apply {
            playSequentially(
                title,
                tvName,
                etName,
                tvEmail,
                etEmail,
                tvPassword,
                etPassword,
                register,
                login
            )
            startDelay = 100
            start()
        }
    }
}