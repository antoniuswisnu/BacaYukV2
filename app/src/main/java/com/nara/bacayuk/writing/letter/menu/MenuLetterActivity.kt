package com.nara.bacayuk.writing.letter.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.databinding.ActivityMenuLetterBinding

class MenuLetterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuLetterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}