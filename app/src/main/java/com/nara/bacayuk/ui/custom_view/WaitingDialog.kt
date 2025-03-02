package com.nara.bacayuk.ui.custom_view

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.nara.bacayuk.databinding.LayoutWaitingDialogBinding

class WaitingDialog(
    context: Context,
    ): Dialog(context) {
    val binding by lazy { LayoutWaitingDialogBinding.inflate(layoutInflater) }
        init {
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        binding.apply {
        }
    }
}

//fun instance waiting dialog
fun Context.waitingDialog() = WaitingDialog(this)

fun WaitingDialog.showDialog() {
    if (!isShowing) {
        show()
    }
}
fun WaitingDialog.hideDialog() {
    if (isShowing) {
        dismiss()
    }
}
