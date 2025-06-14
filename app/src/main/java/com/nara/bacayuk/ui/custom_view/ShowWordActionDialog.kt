package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ShowWordActionDialogBinding

class ShowWordActionDialog (
    context: Context,
    private val icon: Int = R.drawable.ic_baseline_info_24_blue,
    private val title: String,
    private val message: String,
    private val onEditClickListener: () -> Unit,
    private val onDeleteClickListener: () -> Unit
): Dialog(context) {
    private val binding by lazy { ShowWordActionDialogBinding.inflate(layoutInflater) }
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
                onEditClickListener.invoke()
                dismiss()
            }
            btnYes.setOnClickListener {
                onDeleteClickListener.invoke()
                dismiss()
            }
        }
    }
}