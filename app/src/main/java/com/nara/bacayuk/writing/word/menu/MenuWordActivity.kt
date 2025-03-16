package com.nara.bacayuk.writing.word.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ActivityMenuWordBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.writing.word.tracing.TracingWordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuWordActivity : AppCompatActivity(), AdapterListener {

    private lateinit var binding: ActivityMenuWordBinding
    private val menuWordViewModel: MenuWordViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }
    private val listWord = arrayListOf<Tulis>()
    private val adapterWordAdapter by lazy { WordAdapter(this@MenuWordActivity) }
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        menuWordViewModel.reportsWord.observe(this@MenuWordActivity){ response ->
            dialog.dismiss()
            when(response){
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisKata,
                            tulisKata = it.tulisKata,
                            reportTulisKata = it
                        )
                        listWord.add(tulis)
                    }
                    adapterWordAdapter.submitData(listWord, "word")
                }
                is Response.Error -> {
                    Log.e("MenuWordActivity", "Error: ${response.message}")
                }
                else -> {}
            }
        }

        binding.rvKata.apply {
            layoutManager = GridLayoutManager(this@MenuWordActivity, 2)
            adapter = adapterWordAdapter
        }
    }

    override fun onResume(){
        super.onResume()
        menuWordViewModel.getAllReportTulisKataFromFirestore(student?.uuid ?: "-")
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@MenuWordActivity, MainActivity::class.java)
            .apply {
                putExtra("student", student)
            }
        startActivity(intent)
        finish()
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        if (data is Tulis){
            val intent = Intent(this@MenuWordActivity, TracingWordActivity::class.java)
                .apply {
                    putExtra("student", student)
                    putExtra(EXTRA_WORD, data.tulisKata)
                }
            startActivity(intent)
            finish()
        } else {
            Log.e("MenuWordActivity", "Data is not Tulis")
        }
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }

}