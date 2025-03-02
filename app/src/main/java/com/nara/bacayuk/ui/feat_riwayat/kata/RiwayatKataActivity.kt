package com.nara.bacayuk.ui.feat_riwayat.kata

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.databinding.ActivityRiwayatKataBinding
import com.nara.bacayuk.databinding.ItemRiwayatBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatKataActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRiwayatKataBinding.inflate(layoutInflater) }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    var quizSusunKata: ArrayList<SoalKata> = arrayListOf()
    var quizPilganKata: ArrayList<SoalKata> = arrayListOf()
    var listBelajarSuku: MutableList<BelajarSuku> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.txtTitle.text = "Riwayat Baca Kata"

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }
        riwayatViewModel.getAllReportKataFromFirestore(student?.uuid ?: "-").also {
            dialog.show()
        }
        riwayatViewModel.getAllBelajarVokal(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.vokals.observe(this@RiwayatKataActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    if (listBelajarSuku.size < 1){
                        listBelajarSuku = response.data.toMutableList()
                        addViewVokal(listBelajarSuku, true)
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

        riwayatViewModel.reportKata.observe(this@RiwayatKataActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    if (quizPilganKata.size < 1){
                        addView(binding.txtA.txtMateriKapital,response.data.belajarVokal.isADone, "a" ,binding.txtA.imgMateriKapital)
                        addView(binding.txtI.txtMateriKapital,response.data.belajarVokal.isIDone, "i", binding.txtI.imgMateriKapital )
                        addView(binding.txtU.txtMateriKapital,response.data.belajarVokal.isUDone, "u", binding.txtU.imgMateriKapital )
                        addView(binding.txtE.txtMateriKapital,response.data.belajarVokal.isEDone, "e" , binding.txtE.imgMateriKapital)
                        addView(binding.txtO.txtMateriKapital,response.data.belajarVokal.isODone, "o", binding.txtO.imgMateriKapital )
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
                    this@RiwayatKataActivity,
                    if (it.isCorrect) R.drawable.ic_finished else R.drawable.ic_unfinished
                )
            )
            when(type){
                1 -> binding.placeholderLatihanSuku.addView(view.root)
                2 -> binding.placeholderLatihanBaca.addView(view.root)
            }

        }
    }


    private fun addView(view: TextView, isFinished: Boolean, vocal: String, img: ImageView) {
        view.text = when(vocal){
            "a" -> "Huruf A/a"
            "i" -> "Huruf I/i"
            "u" -> "Huruf U/u"
            "e" -> "Huruf E/e"
            "o" -> "Huruf O/o"
            else -> "-"

        }
        img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@RiwayatKataActivity,
                    if (isFinished) R.drawable.ic_finished else R.drawable.ic_unfinished
                )
            )
    }

    private fun addViewVokal(list: MutableList<BelajarSuku>, status: Boolean) {
        list.forEach {
            val view = ItemRiwayatBinding.inflate(layoutInflater)
            val last = it.abjadName.get(1).toString()
            "Kombinasi Huruf ${last}".also { view.txtMateriKapital.text = it }
            view.imgMateriKapital.setImageDrawable(
                ContextCompat.getDrawable(
                    this@RiwayatKataActivity,
                    if (it.belajarVokal.isADone
                        && it.belajarVokal.isIDone
                        && it.belajarVokal.isUDone
                        && it.belajarVokal.isEDone
                        && it.belajarVokal.isODone
                    ) R.drawable.ic_finished else R.drawable.ic_unfinished
                )
            )
            binding.placeholderBelajarSuku.addView(view.root)
        }
    }
}