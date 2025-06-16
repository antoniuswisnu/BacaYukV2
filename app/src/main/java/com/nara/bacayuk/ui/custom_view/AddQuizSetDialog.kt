package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.DialogAddQuizSetBinding

class AddQuizSetDialog (
    context: Context,
    private val icon: Int = R.drawable.ic_add_purple,
    private val title: String,
    private val message: String,
    private var titleQuiz: String = "",
    private var descQuiz: String = "",
    private val onConfirmClickListener: (String, String) -> Unit
): Dialog(context) {
    private val binding by lazy { DialogAddQuizSetBinding.inflate(layoutInflater) }
    init {
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        binding.apply {
            imgIcon.setImageResource(icon)
            txtTitle.text = title
            editTextTitleQuiz.setText(titleQuiz)
            editTextDescQuiz.setText(descQuiz)
            txtMessage.text = message
            btnNo.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                onConfirmClickListener.invoke(binding.editTextTitleQuiz.text.toString().trim(),
                                              binding.editTextDescQuiz.text.toString().trim())
                dismiss()
            }
        }
    }
}