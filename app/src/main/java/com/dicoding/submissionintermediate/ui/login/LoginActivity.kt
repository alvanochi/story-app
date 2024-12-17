package com.dicoding.submissionintermediate.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.databinding.ActivityLoginBinding
import com.dicoding.submissionintermediate.ui.main.MainActivity
import com.dicoding.submissionintermediate.ui.register.RegisterActivity


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupLoginObserver()
        playAnimation()

        binding.tvRedirect.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()

        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            loginViewModel.loginUser(email, password)

            loginViewModel.userLogin.observe(this) {
                val token = it.token.toString()
                loginViewModel.saveSession(token)
            }
        }
    }

    private fun setupErrorDialog(message: String) {
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
        binding.btnLogin.isEnabled = !isEnabled
    }

    private fun moveActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupLoginObserver(){
        loginViewModel.isLoading.observe(this){
            showLoading(it)
            showButton(it)
        }

        loginViewModel.authToken.observe(this){ token ->
            if(token.isNotEmpty()){
                moveActivity()
            }
        }

        loginViewModel.responseLogin.observe(this) {
            setupErrorDialog(it)
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val tvMessage = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.llRegister, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(tvMessage, tvEmail, etEmail, tvPassword, etPassword, login, register)
            startDelay = 100
            start()
        }
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.authToken.observe(this){ token ->
            if(token.isNotEmpty()){
                moveActivity()
            }
        }
    }

}