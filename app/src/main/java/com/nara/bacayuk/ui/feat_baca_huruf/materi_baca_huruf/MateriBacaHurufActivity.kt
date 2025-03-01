package com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Abjad
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMateriBacaHurufBinding
import com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf.MenuBacaHurufActivity
import com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf.QuizBacaHurufActivity
import com.nara.bacayuk.utils.DATA
import com.nara.bacayuk.utils.ToastType
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.showQuizToast

class MateriBacaHurufActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMateriBacaHurufBinding.inflate(layoutInflater) }
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
                txtTitle.text = if (dataAbjad == null) "Baca Kata" else getString(R.string.baca_huruf)
                txtTitle.setTextColor(resources.getColor(R.color.teal_600))
                imgActionRight.invisible()
                imageView.invisible()
                imageView.setOnClickListener {
                    finish()
                    Toast.makeText(this@MateriBacaHurufActivity, "back pressed", Toast.LENGTH_SHORT).show()
                }
            }

            val isKata = dataAbjad == null
            val pagerAdapter = ViewPageAdapter(this@MateriBacaHurufActivity, isKata = isKata) { name = it.toString() }
            slideVP.adapter = pagerAdapter
            dotsIndicator.setViewPager2(slideVP)

            buttonNext.setOnClickListener {
                val n = if (isKata) 4 else 2
                if (slideVP.currentItem < n) {
                    slideVP.currentItem = slideVP.currentItem.plus(1)
                } else {
                    if (!isKata) {
                        val intent = Intent(this@MateriBacaHurufActivity, QuizBacaHurufActivity::class.java)
                            .apply {
                                putExtra(DATA, dataAbjad as Abjad)
                                putExtra("student", student)
                            }
                        startActivity(intent)
                        finish()
                    } else {
                        showQuizToast(this@MateriBacaHurufActivity, ToastType.VOKAL)
                        finish()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (dataAbjad == null) {
            finish()
        } else {
            val intent = Intent(this@MateriBacaHurufActivity, MenuBacaHurufActivity::class.java)
                .apply {
                    putExtra("student", student)
                    putExtra("isKata", false)
                }
            startActivity(intent)
            finish()
        }
    }
}