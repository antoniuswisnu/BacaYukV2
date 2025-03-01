package com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Abjad
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizBacaHurufBinding
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufActivity
import com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf.MenuBacaHurufActivity
import com.nara.bacayuk.utils.DATA
import com.nara.bacayuk.utils.invisible

class QuizBacaHurufActivity : AppCompatActivity() {

    private val binding by lazy { ActivityQuizBacaHurufBinding.inflate(layoutInflater) }
    private var name = ""
    companion object {
        var dataAbjad: Abjad? = null
        var student: Student? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dataAbjad = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DATA, Abjad::class.java)
        } else {
            intent.getParcelableExtra(DATA) as Abjad?
        }

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.apply {

            toolbar.apply {
                txtTitle.text = getString(R.string.baca_huruf)
                txtTitle.setTextColor(resources.getColor(R.color.teal_600))
                imgActionRight.invisible()
                imageView.invisible()
                imageView.setOnClickListener {
                    finish()
                }
            }

            val pagerAdapter = ViewPageQuizAdapter(this@QuizBacaHurufActivity) { name = it.toString() }
            slideVP.adapter = pagerAdapter
            dotsIndicator.setViewPager2(slideVP)

            buttonNext.setOnClickListener {
                if (slideVP.currentItem < 1) {
                    slideVP.currentItem = slideVP.currentItem.plus(1)
                } else {
//                    openActivity(this@MateriBacaHurufActivity, QuizBacaHurufActivity::class.java)
                    val intent = Intent(this@QuizBacaHurufActivity, MenuBacaHurufActivity::class.java)
                        .apply { putExtra("student", student) }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@QuizBacaHurufActivity, MenuBacaHurufActivity::class.java)
            .apply { putExtra("student", MateriBacaHurufActivity.student) }
        startActivity(intent)
        finish()
    }
}