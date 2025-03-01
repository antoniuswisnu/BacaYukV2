package com.nara.bacayuk.writing.number.animation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.writing.number.tracing.TracingNumberActivity
import com.nara.bacayuk.databinding.ActivityNumberAnimationBinding
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufActivity.Companion.student

class NumberAnimationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNumberAnimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        val selectedNumber = intent.getStringExtra("SELECTED_NUMBER") ?: "0"
        binding.numberPathView.setNumber(selectedNumber)

        binding.tvTitle.text = selectedNumber

        binding.btnNext.setOnClickListener {
            startActivity(
                Intent(this, TracingNumberActivity::class.java).apply {
                    putExtra(TracingNumberActivity.EXTRA_NUMBER, selectedNumber)
                    putExtra("student", student)
                }
            )
        }
    }

    companion object {
        const val EXTRA_NUMBER = "SELECTED_NUMBER"
    }
}