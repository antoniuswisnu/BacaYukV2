package com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.databinding.ActivityMenuBacaHurufBinding
import com.nara.bacayuk.ui.customview.waitingDialog
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufActivity
import com.nara.bacayuk.ui.feat_baca_kata.materi.MateriBacaVokalActivity
import com.nara.bacayuk.ui.feat_baca_kata.menu.MenuBacaKataActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.DATA
import com.nara.bacayuk.utils.invisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuBacaHurufActivity : AppCompatActivity(), AdapterListener {

    private val binding by lazy { ActivityMenuBacaHurufBinding.inflate(layoutInflater) }
    private val adapterAbjadMenuAdapter by lazy { AbjadMenuAdapter(this@MenuBacaHurufActivity) }
    var student: Student? = null
    private val dialog by lazy { waitingDialog() }
    private val menuBacaHurufViewModel: MenuBacaHurufViewModel by viewModel()
    private val listAbjadMenu = arrayListOf<Abjad>()
    private var isBacaKata = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        isBacaKata = intent.getBooleanExtra("isKata", false)

        Log.d("menubaca", "${student?.uuid}")

        menuBacaHurufViewModel.vokals.observe(
            this@MenuBacaHurufActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    response.data.forEach {
                        val abjad = Abjad(
                            id = it.abjadName,
                            abjadNonKapital = it.abjadName[1].toString(),
                            abjadKapital = it.abjadName[0].toString(),
                            suara = "-",
                            reportHuruf = null,
                            belajarSuku = it
                        )
                        if (!(listAbjadMenu.contains(abjad)))
                            listAbjadMenu.add(abjad)
                    }
                    if (isBacaKata) adapterAbjadMenuAdapter.submitData(listAbjadMenu, "kata")
                }

                is Response.Error -> {
                    response.message?.let {
                        Log.d("menubaca", it)
                    }
                }

                else -> {}
            }
        }

        menuBacaHurufViewModel.reports.observe(this@MenuBacaHurufActivity) { response ->
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
                    if (!isBacaKata) adapterAbjadMenuAdapter.submitData(listAbjadMenu, "huruf")
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
            toolbarAction.apply {
                //change bg rootview to teal_600
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@MenuBacaHurufActivity,
                        R.color.teal_600)

                //change tint image to white
                imageView.imageTintList = AppCompatResources.getColorStateList(this@MenuBacaHurufActivity,
                        R.color.white)

                txtTitle.setTextColor(AppCompatResources.getColorStateList(this@MenuBacaHurufActivity,
                        R.color.white))

                txtTitle.text = if (isBacaKata) "Baca Kata" else getString(R.string.baca_huruf)
                imageView.setOnClickListener {
                    onBackPressed()
                }
            }
            rvAbjad.apply {
                adapter = adapterAbjadMenuAdapter
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@MenuBacaHurufActivity, 4)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBacaKata) {
            menuBacaHurufViewModel.getAllBelajarVokal(student?.uuid ?: "-")
            dialog.show()
        }
        else {
            menuBacaHurufViewModel.getAllReports(student?.uuid ?: "-")
            dialog.show()
        }
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        Log.d("menubaca", "onClick $isBacaKata")
        val intent1 = Intent(this@MenuBacaHurufActivity, MateriBacaHurufActivity::class.java)
            .apply {
                putExtra(DATA, data as Abjad)
                putExtra("student", student)
            }
        val intent2 = Intent(this@MenuBacaHurufActivity, MateriBacaVokalActivity::class.java)
            .apply {
                putExtra(DATA, data as Abjad)
                putExtra("student", student)
            }

        if (isBacaKata) {
            startActivity(intent2)
        } else {
            startActivity(intent1)
            finish()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        if (isBacaKata) {
            val intent = Intent(this@MenuBacaHurufActivity, MenuBacaKataActivity::class.java)
                .apply {
                    putExtra("student", student)
                }
            startActivity(intent)
            finish()
        } else{
            finish()
        }
    }
}