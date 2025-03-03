package com.nara.bacayuk.ui.feat_riwayat.tulis.huruf

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ActivityRiwayatTulisHurufBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatTulisHurufActivity : AppCompatActivity() {
    
    private val binding by lazy{ ActivityRiwayatTulisHurufBinding.inflate(layoutInflater) }
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    private val riwayatTulisHurufAdapter by lazy { RiwayatTulisHurufAdapter() }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val listTulisHurufMenu = arrayListOf<Tulis>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_tulis_huruf)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        riwayatViewModel.getAllReportTulisHurufFromFirestore(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.reportTulisHuruf.observe(this@RiwayatTulisHurufActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisHuruf,
                            tulisHuruf = it.tulisHuruf,
                            reportTulisHuruf = it
                        )
                        listTulisHurufMenu.add(tulis)
                    }
                    riwayatTulisHurufAdapter.submitData(listTulisHurufMenu)
                }
                is Response.Error -> {
                    response.message?.let {
                        Log.d("menuhuruf", it)
                    }
                }
                else -> {}
            }
        }

        binding.apply {
            rvRiwayatTulisHuruf.apply {
                adapter = riwayatTulisHurufAdapter
                layoutManager = LinearLayoutManager(this@RiwayatTulisHurufActivity)
            }
        }
    }
}