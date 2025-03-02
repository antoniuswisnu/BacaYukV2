package com.nara.bacayuk.ui.feat_auth.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityLoginBinding
import com.nara.bacayuk.ui.customview.waitingDialog
import com.nara.bacayuk.ui.feat_auth.forgot_password.ForgotPasswordActivity
import com.nara.bacayuk.ui.feat_auth.register.RegisterActivity
import com.nara.bacayuk.ui.feat_student.list_student.ListStudentActivity
import com.nara.bacayuk.utils.changeErrorStateEditText
import com.nara.bacayuk.utils.isValidEmail
import com.nara.bacayuk.utils.openActivity
import com.nara.bacayuk.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val dialog by lazy { this@LoginActivity.waitingDialog() }
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            toolbar.txtTitle.text = getString(R.string.masuk)
            onTextChange()
            btnLogin.isEnabled =
                edtEmail.text.toString().isNotEmpty() && edtPassword.text.toString().isNotEmpty()
            txtDaftar.setOnClickListener {
                openActivity(this@LoginActivity, RegisterActivity::class.java)
            }
            txtForgotPassword.setOnClickListener {
                openActivity(this@LoginActivity, ForgotPasswordActivity::class.java)
            }
        }


        loginViewModel.user.observe(this@LoginActivity) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d("LoginActivity", "onCreate: ${response.data.email}")
                    loginViewModel.saveUserToDataStore(response.data)
                    getUserDataStore()
                }
                is Response.Error -> {
                    Toast.makeText(this, response.e, Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "onCreate: ${response.message}")
                }
                else -> {

                }
            }
        }

        loginViewModel.errorMessage.observe(this@LoginActivity) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loginViewModel.user.observe(this@LoginActivity) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d("LoginActivity", "onCreate: ${response.data.email}")
                    loginViewModel.saveUserToDataStore(response.data)
                    getUserDataStore()
                }
                is Response.Error -> {
                    Toast.makeText(this, response.e, Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "onCreate: ${response.message}")
                }
                else -> {

                }
            }
        }
    }

    private fun getUserDataStore() {
        val user: User? = loginViewModel.getUserDataStore()
        if (user != null && user.email != "") {
            openActivity(this@LoginActivity, ListStudentActivity::class.java)
        }
    }

    private fun ActivityLoginBinding.onTextChange() {
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (edtEmail.text.toString().isEmpty()) {
                    changeErrorStateEditText(
                        this@LoginActivity,
                        edtEmail,
                        txtEmailError,
                        "Email tidak boleh kosong",
                        true
                    )
                } else {
                    if (isValidEmail(edtEmail.text.toString())) {
                        changeErrorStateEditText(
                            this@LoginActivity,
                            edtEmail,
                            txtEmailError,
                            "Email tidak boleh kosong",
                            false
                        )
                    } else {
                        changeErrorStateEditText(
                            this@LoginActivity,
                            edtEmail,
                            txtEmailError,
                            "Email tidak valid",
                            true
                        )
                    }
                }
                btnLogin.isEnabled =
                    edtEmail.text.toString().isNotEmpty() && edtPassword.text.toString()
                        .isNotEmpty()
                btnLogin.setOnClickListener {
                    loginViewModel.login(edtEmail.text.toString(), edtPassword.text.toString())
                    //handler delay 2 s
                    dialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        getUserDataStore()
                        dialog.dismiss()
                    }, 2000)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (edtPassword.text.toString().isEmpty()) {
                    changeErrorStateEditText(
                        this@LoginActivity,
                        edtPassword,
                        txtPasswordError,
                        "Password tidak boleh kosong",
                        true
                    )
                } else {
                    if (edtPassword.text.toString().length < 8) {
                        changeErrorStateEditText(
                            this@LoginActivity,
                            edtPassword,
                            txtPasswordError,
                            "Password minimal 8 karakter",
                            true
                        )
                    } else {
                        changeErrorStateEditText(
                            this@LoginActivity,
                            edtPassword,
                            txtPasswordError,
                            "Password minimal 8 karakter",
                            false
                        )
                    }
                }
                btnLogin.isEnabled =
                    edtEmail.text.toString().isNotEmpty() && edtPassword.text.toString()
                        .isNotEmpty()
                btnLogin.setOnClickListener {
                    loginViewModel.login(edtEmail.text.toString(), edtPassword.text.toString())
                    //handler delay 2 s
                    dialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        getUserDataStore()
                        dialog.dismiss()
                    }, 2000)


                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("onback", "onBackPressed")
    }
}