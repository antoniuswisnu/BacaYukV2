package com.nara.bacayuk.ui.feat_auth.forgot_password

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.databinding.ActivityForgotPasswordBinding
import com.nara.bacayuk.ui.custom_view.ConfirmationDialog
import com.nara.bacayuk.utils.invisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : AppCompatActivity() {
    
    private val binding by lazy { ActivityForgotPasswordBinding.inflate(layoutInflater) }
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            toolbarAction.apply {
                imageView.setOnClickListener { finish() }
                imgActionRight.invisible()
                txtTitle.text = "Lupa Katasandi"
            }
            btnLogin.setOnClickListener {
                val dialog = ConfirmationDialog(
                    this@ForgotPasswordActivity,
                    title = "Konfirmasi Pengiriman",
                    message = "Apakah Anda yakin email yang telah Anda entri benar?",
                    onConfirmClickListener = {
                        forgotPasswordViewModel.forgotPassword(edtEmail.text.toString())
                        Toast.makeText(this@ForgotPasswordActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
                dialog.show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}