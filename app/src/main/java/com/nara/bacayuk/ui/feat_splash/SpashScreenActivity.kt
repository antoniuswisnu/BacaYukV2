package com.nara.bacayuk.ui.feat_splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.nara.bacayuk.databinding.ActivitySpashScreenBinding
import com.nara.bacayuk.ui.feat_student.list_student.ListStudentActivity

class SpashScreenActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySpashScreenBinding.inflate(layoutInflater) }
    private val splashTimeOut: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        object : CountDownTimer(splashTimeOut, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Tidak perlu melakukan apa-apa pada setiap tick
            }

            override fun onFinish() {
                val intent = Intent(this@SpashScreenActivity, ListStudentActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}