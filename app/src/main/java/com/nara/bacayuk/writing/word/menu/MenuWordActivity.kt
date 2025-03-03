package com.nara.bacayuk.writing.word.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.word.tracing.TracingWordActivity
import com.nara.bacayuk.databinding.ActivityMenuWordBinding

class MenuWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGrid()
    }

    private fun setupGrid() {
       val word = listOf("Buku", "Pensil", "Mouse")
        val adapter = WordAdapter(this, word)

        adapter.setOnWordClickListener(object : WordAdapter.OnWordClickListener {
            override fun onWordClick(word: String) {
                val intent = Intent(this@MenuWordActivity, TracingWordActivity::class.java).apply {
                    putExtra(TracingWordActivity.EXTRA_WORD, word)
                }
                startActivity(intent)
            }
        })

        binding.gridView.adapter = adapter
    }
}