package com.nara.bacayuk.writing.number.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMenuNumberBinding
import com.nara.bacayuk.ui.customview.waitingDialog
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.writing.number.animation.NumberAnimationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuNumberBinding
    private val menuNumberViewModel: MenuNumberViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }

    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

//        menuNumberViewModel.reportsNumber.observe(this@MenuNumberActivity) { response ->
//            dialog.dismiss()
//            when(response){
//                is Response.Success -> {
//                    response.data.forEach(
//                        val
//                    )
//                }
//            }
//        }


        setupGrid()
    }

    override fun onResume() {
        super.onResume()
        menuNumberViewModel.getAllReportTulisAngkaFromFirestore(student?.uuid ?: "-")
        dialog.show()
    }

    private fun setupGrid() {
        val number = ('0'..'9').toList()
        val adapter = NumberAdapter(this, number)

        adapter.setOnNumberClickListener(object : NumberAdapter.OnNumberClickListener {
            override fun onNumberClick(number: String) {
                val intent = Intent(this@MenuNumberActivity, NumberAnimationActivity::class.java).apply {
                    putExtra(NumberAnimationActivity.EXTRA_NUMBER, number)
                    putExtra("student", student)
                }
                startActivity(intent)
            }
        })

        binding.gridView.adapter = adapter
    }
}