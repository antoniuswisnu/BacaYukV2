package com.nara.bacayuk.ui.feat_baca_kata.quiz

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.databinding.ActivityQuizMenuBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_belajar_kalimat.QuizKalimatActivity
import com.nara.bacayuk.ui.feat_belajar_kalimat.QuizPilganKalimatActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizMenuActivity : AppCompatActivity(), AdapterListener {

    private val binding by lazy { ActivityQuizMenuBinding.inflate(layoutInflater) }
    private val adapterQuizMenuAdapter1 by lazy { QuizMenuAdapter(this@QuizMenuActivity, "susun") }
    private val adapterQuizMenuAdapter2 by lazy { QuizMenuAdapter(this@QuizMenuActivity,"pilgan") }
    private val quizViewModel: QuizViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }
    var reportKata: ReportKata? = null
    var reportKalimat: ReportKalimat? = null
    var student: Student? = null
    var isKata = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        isKata = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getBooleanExtra("isKata",false)
        } else {
            intent.getBooleanExtra("isKata",false)
        }

        Log.d("idstudent", "onCreate ${student?.uuid}")


        binding.apply {
            toolbarAction.apply {
                txtTitle.text =if(isKata) "Baca Kata" else "Baca Kalimat"
                imageView.setOnClickListener { onBackPressed() }
                imgActionRight.invisible()
                txtSusun.text = if(isKata) "Susun Kata" else "Susun Kalimat"
                txtBaca.text = if(isKata) "Latihan Baca Kata" else "Latihan Baca Kalimat"

                //change bg rootview to teal_600
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@QuizMenuActivity,
                    R.color.blue_500)

                //change tint image to white
                imageView.imageTintList = AppCompatResources.getColorStateList(this@QuizMenuActivity,
                    R.color.white)

                txtTitle.setTextColor(
                    AppCompatResources.getColorStateList(this@QuizMenuActivity,
                        R.color.white))

                imageView.setOnClickListener {
                    finish()
                }
            }


            rvSusunKata.apply {
                adapter = adapterQuizMenuAdapter1
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@QuizMenuActivity, 4)
            }
            rvBacaKata.apply {
                adapter = adapterQuizMenuAdapter2
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@QuizMenuActivity, 4)
            }
        }

        quizViewModel.reportKatas.observe(this@QuizMenuActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    reportKata = response.data
                    adapterQuizMenuAdapter1.submitData(response.data.quizSusunKata)
                    adapterQuizMenuAdapter2.submitData(response.data.quizPilganKata)

                    Log.d("reportKatas", response.data.quizSusunKata.toString())
                }
                else -> {

                }
            }
        }

        quizViewModel.reportKalimat.observe(this@QuizMenuActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    reportKalimat = response.data
                    adapterQuizMenuAdapter1.submitData(response.data.quizSusunKata)
                    adapterQuizMenuAdapter2.submitData(response.data.quizPilganKata)
                    Log.d("reportKatas", response.data.quizSusunKata.toString())
                }
                else -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isKata) {
            quizViewModel.getAllReportKataFromFirestore(student?.uuid ?: "-")
            dialog.show()
        }
        else {
            quizViewModel.getAllReportKalimatFromFirestore(student?.uuid?: "-")
            dialog.show()
        }
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        when(type) {
            "pilgan" -> {
                val intent = Intent(this@QuizMenuActivity, QuizPilganKalimatActivity::class.java).apply {
                    putExtra("student", student)
                    putExtra("quiz", data as SoalKata)
                    putExtra("isKata", isKata)
                    putExtra("data", if (isKata) reportKata else reportKalimat)
                }
                startActivity(intent)
            }
            "susun" -> {
                val intent = Intent(this@QuizMenuActivity, QuizKalimatActivity::class.java).apply {
                    putExtra("student", student)
                    putExtra("quiz", data as SoalKata)
                    putExtra("isKata", isKata)
                    putExtra("data", if (isKata) reportKata else reportKalimat)
                }
                startActivity(intent)
            }
        }
    }

}