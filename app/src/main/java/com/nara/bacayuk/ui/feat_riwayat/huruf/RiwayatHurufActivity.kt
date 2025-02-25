package com.nara.bacayuk.ui.feat_riwayat.huruf

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.data.model.Abjad
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityRiwayatHurufBinding
import com.nara.bacayuk.ui.customview.waitingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatHurufActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRiwayatHurufBinding.inflate(layoutInflater) }
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    private val riwayatHurufAdapter by lazy { RiwayatHurufAdapter() }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val listAbjadMenu = arrayListOf<Abjad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        riwayatViewModel.getAllReports(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.reports.observe(this@RiwayatHurufActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    response.data.forEach {
                        //get index 1 of string
                        val abjad = Abjad(
                            id = it.abjadName,
                            abjadNonKapital = it.abjadName[1].toString(),
                            abjadKapital = it.abjadName[0].toString(),
                            suara = "-",
                            reportHuruf = it
                        )
                        listAbjadMenu.add(abjad)
                    }
                    riwayatHurufAdapter.submitData(listAbjadMenu)
                }

                is Response.Error -> {
                    response.message?.let {
                        Log.d("menubaca", it)
                    }
                }

                else -> {}
            }
        }

        binding.apply {
            toolbar.txtTitle.text = "Riwayat Baca Huruf"
            rvRiwayatHuruf.apply {
                adapter = riwayatHurufAdapter
                layoutManager = LinearLayoutManager(this@RiwayatHurufActivity)
            }
        }
    }
}