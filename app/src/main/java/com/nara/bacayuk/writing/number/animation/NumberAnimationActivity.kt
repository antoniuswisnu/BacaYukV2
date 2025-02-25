package com.nara.bacayuk.writing.number.animation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.number.tracing.TracingNumberActivity
import com.nara.bacayuk.databinding.ActivityNumberAnimationBinding

class NumberAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumberAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedNumber = intent.getStringExtra("SELECTED_NUMBER") ?: "0"
        binding.numberPathView.setNumber(selectedNumber)

        binding.tvTitle.text = selectedNumber

        binding.btnNext.setOnClickListener {
            startActivity(
                Intent(this, TracingNumberActivity::class.java).apply {
                    putExtra(TracingNumberActivity.EXTRA_NUMBER, selectedNumber)
                }
            )
        }
    }

    companion object {
        const val EXTRA_NUMBER = "SELECTED_NUMBER"
    }
}