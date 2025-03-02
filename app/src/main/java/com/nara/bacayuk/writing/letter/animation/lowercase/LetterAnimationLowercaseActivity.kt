package com.nara.bacayuk.writing.letter.animation.lowercase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.letter.tracing.capital.TracingLetterCapitalActivity
import com.nara.bacayuk.databinding.ActivityLetterAnimationLowercaseBinding

class LetterAnimationLowercaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLetterAnimationLowercaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterAnimationLowercaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedLetter = intent.getStringExtra("SELECTED_LETTER") ?: "a"
        binding.letterPathView.setLetter(selectedLetter.lowercase())

        binding.tvTitle.text = selectedLetter.lowercase()

        binding.btnNext.setOnClickListener {
            startActivity(
                Intent(this, TracingLetterCapitalActivity::class.java).apply {
                    putExtra(TracingLetterCapitalActivity.EXTRA_LETTER, selectedLetter)
                }
            )
        }
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}