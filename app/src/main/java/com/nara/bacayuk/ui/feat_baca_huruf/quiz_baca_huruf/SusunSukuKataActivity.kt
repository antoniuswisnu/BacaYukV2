package com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nara.bacayuk.databinding.ActivitySusunSukuKataBinding

class SusunSukuKataActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySusunSukuKataBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            binding.imageView2.setOnClickListener {
                moveView(imageView2, imageView3)
            }
        }
    }

    private fun moveView(viewToBeMoved: View, targetView: View) {
        val targetX: Float =
            targetView.x + targetView.width / 2 - viewToBeMoved.width / 2
        val targetY: Float =
            targetView.y + targetView.height / 2 - viewToBeMoved.height / 2

        viewToBeMoved.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(1000)
            .withEndAction {
                targetView.visibility = View.GONE
            }
            .start()
    }


}