package com.android.bisabelajar.ui.feat_auth.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.android.bisabelajar.R
import com.android.bisabelajar.data.model.Response
import com.android.bisabelajar.databinding.ActivityRegisterBinding
import com.android.bisabelajar.ui.feat_auth.login.LoginActivity
import com.android.bisabelajar.ui.feat_menu_utama.MainActivity
import com.android.bisabelajar.utils.changeErrorStateEditText
import com.android.bisabelajar.utils.isValidEmail
import com.android.bisabelajar.utils.openActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            toolbar.txtTitle.text = getString(R.string.daftar)
            onTextChange()
            txtMasuk.setOnClickListener {
                openActivity(this@RegisterActivity, LoginActivity::class.java)
            }
            btnDaftar.isEnabled = edtName.text.toString().isNotEmpty() && edtEmail.text.toString()
                .isNotEmpty() && edtPassword.text.toString()
                .isNotEmpty() && edtPasswordConfirmation.text.toString().isNotEmpty()
        }

        registerViewModel.user.observe(this@RegisterActivity) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d("LoginActivity", "onCreate: ${response.data.email}")
                    openActivity(this@RegisterActivity, MainActivity::class.java)
                }
                is Response.Error -> {
                    Toast.makeText(this, "${response.e?.message}", Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "onCreate: ${response.message}")
                }
                else -> {

                }
            }
        }

        registerViewModel.errorMessage.observe(this@RegisterActivity) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    }

    private fun onTextChange() {
        binding.apply {
            edtName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (edtName.text.toString().isEmpty()) {
                        changeErrorStateEditText(
                            this@RegisterActivity,
                            edtName,
                            txtNameError,
                            getString(R.string.nama_lengkap_wajib_diisi),
                            true
                        )
                    } else {
                        changeErrorStateEditText(
                            this@RegisterActivity,
                            edtName,
                            txtNameError,
                            getString(R.string.nama_lengkap_wajib_diisi),
                            false
                        )

                    }
                    btnDaftar.isEnabled = s.toString().isNotEmpty() && edtEmail.text.toString()
                        .isNotEmpty() && edtPassword.text.toString()
                        .isNotEmpty() && edtPasswordConfirmation.text.toString().isNotEmpty()
                    btnDaftar.setOnClickListener {
                        registerViewModel.register(
                            edtEmail.text.toString(),
                            edtPassword.text.toString()
                        )
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // do nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // do nothing
                }
            })

            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    if (edtEmail.text.toString().isEmpty()) {
                        changeErrorStateEditText(
                            this@RegisterActivity,
                            edtEmail,
                            txtEmailError,
                            "Email tidak boleh kosong",
                            true
                        )
                    } else {
                        if (isValidEmail(edtEmail.text.toString())) {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtEmail,
                                txtEmailError,
                                "Email tidak boleh kosong",
                                false
                            )
                        } else {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtEmail,
                                txtEmailError,
                                "Email tidak valid",
                                true
                            )
                        }
                    }
                    btnDaftar.isEnabled =
                        s.toString().isNotEmpty() && edtEmail.text.toString()
                            .isNotEmpty() && edtPassword.text.toString()
                            .isNotEmpty() && edtPasswordConfirmation.text.toString().isNotEmpty()
                    btnDaftar.setOnClickListener {
                        registerViewModel.register(
                            edtEmail.text.toString(),
                            edtPassword.text.toString()
                        )
                    }
                }
            })


            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (edtPassword.text.toString().isEmpty()) {
                        changeErrorStateEditText(
                            this@RegisterActivity,
                            edtPassword,
                            txtPasswordError,
                            "Password tidak boleh kosong",
                            true
                        )
                    } else {
                        if (edtPassword.text.toString().length < 8) {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtPassword,
                                txtPasswordError,
                                "Password minimal 8 karakter",
                                true
                            )
                        } else {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtPassword,
                                txtPasswordError,
                                "Password minimal 8 karakter",
                                false
                            )
                        }
                    }
                    btnDaftar.isEnabled =
                        s.toString().isNotEmpty() && edtEmail.text.toString()
                            .isNotEmpty() && edtPassword.text.toString()
                            .isNotEmpty() && edtPasswordConfirmation.text.toString().isNotEmpty()
                    btnDaftar.setOnClickListener {
                        registerViewModel.register(
                            edtEmail.text.toString(),
                            edtPassword.text.toString()
                        )
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

            edtPasswordConfirmation.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (edtPasswordConfirmation.text.toString().isEmpty()) {
                        changeErrorStateEditText(
                            this@RegisterActivity,
                            edtPassword,
                            txtPasswordConfirmationError,
                            "Password tidak boleh kosong",
                            true
                        )
                    } else {
                        if (edtPasswordConfirmation.text.toString().length < 8) {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtPassword,
                                txtPasswordConfirmationError,
                                "Password minimal 8 karakter",
                                true
                            )
                        } else if (edtPassword.text.toString() != edtPasswordConfirmation.text.toString()) {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtPassword,
                                txtPasswordConfirmationError,
                                "Password tidak sama",
                                true
                            )
                        } else {
                            changeErrorStateEditText(
                                this@RegisterActivity,
                                edtPassword,
                                txtPasswordConfirmationError,
                                "Password minimal 8 karakter",
                                false
                            )
                        }
                    }
                    btnDaftar.isEnabled =
                        s.toString().isNotEmpty() && edtEmail.text.toString()
                            .isNotEmpty() && edtPassword.text.toString()
                            .isNotEmpty() && edtPasswordConfirmation.text.toString().isNotEmpty()
                    btnDaftar.setOnClickListener {
                        registerViewModel.register(
                            edtEmail.text.toString(),
                            edtPassword.text.toString()
                        )
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
    }
}