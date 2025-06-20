package com.nara.bacayuk.writing.letter.animation.capital

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.writing.letter.animation.lowercase.LetterAnimationLowercaseActivity
import com.nara.bacayuk.databinding.ActivityLetterAnimationCapitalBinding

class LetterAnimationCapitalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLetterAnimationCapitalBinding
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterAnimationCapitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        val selectedLetter = intent.getStringExtra("SELECTED_LETTER") ?: "A"
        binding.letterPathView.setLetter(selectedLetter)

        binding.tvTitle.text = selectedLetter

        binding.btnNext.setOnClickListener {
            startActivity(
                Intent(this, LetterAnimationLowercaseActivity::class.java).apply {
                    putExtra(LetterAnimationLowercaseActivity.EXTRA_LETTER, selectedLetter)
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