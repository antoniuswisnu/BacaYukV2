package com.nara.bacayuk.ui.feat_riwayat.kalimat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.SoalKata
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityRiwayatKalimatBinding
import com.nara.bacayuk.databinding.ItemRiwayatBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatKalimatActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRiwayatKalimatBinding.inflate(layoutInflater) }
    var student: Student? = null
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    private var quizSusunKata: ArrayList<SoalKata> = arrayListOf()
    private var quizPilganKata: ArrayList<SoalKata> = arrayListOf()
    private val dialog by lazy { waitingDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.txtTitle.text = "Riwayat Baca Kalimat"

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        riwayatViewModel.getAllReportKalimatFromFirestore(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.reportKalimat.observe(this@RiwayatKalimatActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    if (quizPilganKata.size < 1){
                        quizSusunKata = response.data.quizSusunKata
                        quizPilganKata = response.data.quizPilganKata
                        addView(quizSusunKata,1)
                        addView(quizPilganKata, 2)
                    }
                }

                is Response.Error -> {
                    response.message?.let {
                        Log.d("menubaca", it)
                    }
                }

                else -> {}
            }
        }


    }

    private fun addView(list: ArrayList<SoalKata>, type: Int) {
        list.forEach {
            val view = ItemRiwayatBinding.inflate(layoutInflater)
            "Level ${it.level}".also { view.txtMateriKapital.text = it }
            view.imgMateriKapital.setImageDrawable(
                ContextCompat.getDrawable(
                    this@RiwayatKalimatActivity,
                    if (it.isCorrect) R.drawable.ic_finished else R.drawable.ic_unfinished
                )
            )
            when(type){
                1 -> binding.placeholderLatihanSuku.addView(view.root)
                2 -> binding.placeholderLatihanBaca.addView(view.root)
            }

        }
    }
}