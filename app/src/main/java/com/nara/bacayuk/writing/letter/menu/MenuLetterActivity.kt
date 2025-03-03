package com.nara.bacayuk.writing.letter.menu

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
import com.nara.bacayuk.databinding.ActivityMenuLetterBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.writing.letter.animation.capital.LetterAnimationCapitalActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuLetterActivity : AppCompatActivity(), AdapterListener {

    private lateinit var binding: ActivityMenuLetterBinding
    private val menuLetterViewModel: MenuLetterViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }
    private val listLetter = arrayListOf<Tulis>()
    private val adapterLetterAdapter by lazy { AlphabetAdapter(this@MenuLetterActivity) }
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        menuLetterViewModel.reportsLetter.observe(this@MenuLetterActivity){ response ->
            dialog.dismiss()
            when(response){
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisHuruf,
                            tulisHuruf = it.tulisHuruf,
                            reportTulisHuruf = it
                        )
                        listLetter.add(tulis)
                    }
                    adapterLetterAdapter.submitData(listLetter, "letter")
                }
                is Response.Error -> {
                    Log.e("MenuLetterActivity", "Error: ${response.message}")
                }
                else -> {}
            }
        }

        binding.rvTulisHuruf.apply{
            layoutManager = GridLayoutManager(this@MenuLetterActivity, 4)
            adapter = adapterLetterAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        menuLetterViewModel.getAllReportTulisHurufFromFirestore(student?.uuid ?: "-")
        dialog.show()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
            .apply {
                putExtra("student", student)
            }
        startActivity(intent)
        finish()
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        if (data is Tulis){
            val letterString = data.tulisHuruf
            val intent = Intent(this, LetterAnimationCapitalActivity::class.java)
                .apply {
                    putExtra("student", student)
                    putExtra(LetterAnimationCapitalActivity.EXTRA_LETTER, letterString)
                }
            startActivity(intent)
        } else {
            throw IllegalArgumentException("Data must be Tulis")
        }
    }
}