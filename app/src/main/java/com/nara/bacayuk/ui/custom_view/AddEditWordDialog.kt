package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.DialogAddEditWordBinding

class AddEditWordDialog (
    context: Context,
    private val icon: Int = R.drawable.ic_baseline_info_24,
    private val title: String,
    private val message: String,
    private var editTextWord: String = "",
    private val onConfirmClickListener: (String) -> Unit
): Dialog(context) {
    private val binding by lazy { DialogAddEditWordBinding.inflate(layoutInflater) }
    init {
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        binding.apply {
            imgIcon.setImageResource(icon)
            txtTitle.text = title
            editTextWordContent.setText(editTextWord)
            txtMessage.text = message
            btnNo.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                onConfirmClickListener.invoke(binding.editTextWordContent.text.toString().trim())
                dismiss()
            }
        }
    }
    fun getEnteredText(): String {
        return binding.editTextWordContent.text.toString().trim()
    }
}