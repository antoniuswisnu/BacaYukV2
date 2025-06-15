package com.nara.bacayuk.writing.letter.animation.lowercase

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.writing.letter.tracing.capital.TracingLetterCapitalActivity
import com.nara.bacayuk.databinding.ActivityLetterAnimationLowercaseBinding

class LetterAnimationLowercaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLetterAnimationLowercaseBinding
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterAnimationLowercaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        val selectedLetter = intent.getStringExtra("SELECTED_LETTER") ?: "a"
        binding.letterPathView.setLetter(selectedLetter.lowercase())

        binding.tvTitle.text = selectedLetter.lowercase()

        binding.btnNext.setOnClickListener {
            startActivity(
                Intent(this, TracingLetterCapitalActivity::class.java).apply {
                    putExtra(TracingLetterCapitalActivity.EXTRA_LETTER, selectedLetter)
                    putExtra("student", student)
                }
            )
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}