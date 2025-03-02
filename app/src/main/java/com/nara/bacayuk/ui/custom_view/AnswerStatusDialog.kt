package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import android.widget.Button
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.LayoutStatusAnswerBinding
import com.nara.bacayuk.utils.playAudioFromRawAssetsFileString

class AnswerStatusDialog(
    context: Context,
    private val icon: Int = R.drawable.ic_checklist,
    private val status: String,
    private val listener: OnDismissDialog

    ): Dialog(context) {
    private val binding by lazy { LayoutStatusAnswerBinding.inflate(layoutInflater) }
        init {
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        binding.apply {
            imgIcon.setImageResource(icon)
            txtStatus.text = "Jawaban $status"
            btnSelect.setOnClickListener {
                listener.onDismissDialog()
                dismiss()
            }
            when(status){
                "Benar" -> {
                    playAudioFromRawAssetsFileString(context,"sound_correct")
                }
                "Salah" -> {
                    playAudioFromRawAssetsFileString(context,"sound_wrong")
                }
            }
        }
    }
}

interface OnDismissDialog{
    fun onDismissDialog()
}
interface OnDialogShow{
    fun onDialogShow(button: Button)
}