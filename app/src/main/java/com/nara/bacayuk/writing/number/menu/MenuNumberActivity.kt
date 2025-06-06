package com.nara.bacayuk.writing.number.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ActivityMenuNumberBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.writing.number.animation.NumberAnimationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuNumberActivity : AppCompatActivity(), AdapterListener {

    private lateinit var binding: ActivityMenuNumberBinding
    private val menuNumberViewModel: MenuNumberViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }
    private val listNumber = arrayListOf<Tulis>()
    private val adapterNumberAdapter by lazy { NumberAdapter(this@MenuNumberActivity) }
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

        binding.apply{
            toolbarAction.apply {
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@MenuNumberActivity,
                    R.color.teal_600)

                imageView.imageTintList = AppCompatResources.getColorStateList(this@MenuNumberActivity,
                    R.color.white)

                txtTitle.setTextColor(
                    AppCompatResources.getColorStateList(this@MenuNumberActivity,
                        R.color.white))

                txtTitle.text = "Tulis Angka"
                imageView.setOnClickListener {
                    onBackPressed()
                }
            }
        }

        menuNumberViewModel.reportsNumber.observe(this@MenuNumberActivity){ response ->
            dialog.dismiss()
            when(response){
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisAngka,
                            tulisAngka = it.tulisAngka,
                            reportTulisAngka = it
                        )
                        listNumber.add(tulis)
                    }
                    adapterNumberAdapter.submitData(listNumber, "number")
                }
                is Response.Error -> {
                    Log.e("MenuNumberActivity", "Error: ${response.message}")
                }
                else -> {}
            }
        }

        binding.rvAngka.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@MenuNumberActivity, 4)
            adapter = adapterNumberAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        menuNumberViewModel.getAllReportTulisAngkaFromFirestore(student?.uuid ?: "-")
        dialog.show()
    }

    @Deprecated("This method has been deprecated in favor of using the\n {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@MenuNumberActivity, MainActivity::class.java)
            .apply {
                putExtra("student", student)
            }
        startActivity(intent)
        finish()
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        if (data is Tulis) {
            val numberString = data.tulisAngka
            val intent = Intent(this@MenuNumberActivity, NumberAnimationActivity::class.java).apply {
                putExtra(NumberAnimationActivity.EXTRA_NUMBER, numberString)
                putExtra("student", student)
            }
            startActivity(intent)
        } else {
            Log.e("MenuNumberActivity", "Unexpected data type: ${data?.javaClass?.simpleName}")
        }
    }
}