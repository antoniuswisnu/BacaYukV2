package com.nara.bacayuk.ui.feat_riwayat.tulis.kata

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ActivityRiwayatTulisKataBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RiwayatTulisKataActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRiwayatTulisKataBinding.inflate(layoutInflater) }
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    private val riwayatTulisKataAdapter by lazy { RiwayatTulisKataAdapter() }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val listTulisKataMenu = arrayListOf<Tulis>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        riwayatViewModel.getAllReportTulisKataFromFirestore(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.reportTulisKata.observe(this@RiwayatTulisKataActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisKata,
                            tulisKata = it.tulisKata,
                            reportTulisKata = it
                        )
                        listTulisKataMenu.add(tulis)
                    }
                    riwayatTulisKataAdapter.submitData(listTulisKataMenu)
                }
                is Response.Error -> {
                    response.message?.let {
                        Log.d("menukata", it)
                    }
                }
                else -> {}
            }
        }

        binding.apply {
            toolbar.txtTitle.text = "Riwayat Tulis Kata"
            rvRiwayatTulisKata.apply{
                layoutManager = LinearLayoutManager(this@RiwayatTulisKataActivity)
                adapter = riwayatTulisKataAdapter
            }
        }
    }
}