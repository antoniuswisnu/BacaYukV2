package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.DialogConfirmationRedStyleBinding

class ConfirmationDialogRedStyle(
    context: Context,
    private val icon: Int = R.drawable.ic_baseline_info_24,
    private val title: String,
    private val message: String,
    private val onConfirmClickListener: () -> Unit
    ): Dialog(context) {
    private val binding by lazy { DialogConfirmationRedStyleBinding.inflate(layoutInflater) }
        init {
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        binding.apply {
            imgIcon.setImageResource(icon)
            txtTitle.text = title
            txtMessage.text = message
            btnNo.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                onConfirmClickListener.invoke()
                dismiss()
            }
        }
    }
}