package com.nara.bacayuk.ui.feat_riwayat.tulis.angka

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityRiwayatTulisAngkaBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RiwayatTulisAngkaActivity : AppCompatActivity() {

    private val binding by lazy{ ActivityRiwayatTulisAngkaBinding.inflate(layoutInflater) }
    private val riwayatViewModel: RiwayatViewModel by viewModel()
    private val riwayatTulisAngkaAdapter by lazy { RiwayatTulisAngkaAdapter() }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val listTulisMenu = arrayListOf<Tulis>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        riwayatViewModel.getAllReportTulisAngkaFromFirestore(student?.uuid ?: "-").also {
            dialog.show()
        }

        riwayatViewModel.reportTulisAngka.observe(this@RiwayatTulisAngkaActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    response.data.forEach{
                        val tulis = Tulis(
                            id = it.tulisAngka,
                            tulisAngka = it.tulisAngka,
                            reportTulisAngka = it
                        )
                        listTulisMenu.add(tulis)
                    }
                    riwayatTulisAngkaAdapter.submitData(listTulisMenu)
                }
                is Response.Error -> {
                    response.message?.let {
                        Log.d("menuangka", it)
                    }
                }
                else -> {}
            }
        }

        binding.apply {
            toolbar.txtTitle.text = "Riwayat Tulis Angka"
            rvRiwayatTulisAngka.apply {
                adapter = riwayatTulisAngkaAdapter
                layoutManager = LinearLayoutManager(this@RiwayatTulisAngkaActivity)
            }
        }

    }
}