package com.nara.bacayuk.writing.number

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.databinding.ActivityMenuNumberBinding
import com.nara.bacayuk.writing.number.animation.NumberAnimationActivity

class MenuNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupGrid()
    }

    private fun setupGrid() {
        val number = ('0'..'9').toList()
        val adapter = NumberAdapter(this, number)

        adapter.setOnNumberClickListener(object : NumberAdapter.OnNumberClickListener {
            override fun onNumberClick(number: String) {
                val intent = Intent(this@MenuNumberActivity, NumberAnimationActivity::class.java).apply {
                    putExtra(NumberAnimationActivity.EXTRA_NUMBER, number)
                }
                startActivity(intent)
            }
        })

        binding.gridView.adapter = adapter
    }
}