package com.nara.bacayuk.ui.feat_menu_utama

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityMainBinding
import com.nara.bacayuk.ui.custom_view.ConfirmationDialogRedStyle
import com.nara.bacayuk.ui.feat_auth.login.LoginActivity
import com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf.MenuBacaHurufActivity
import com.nara.bacayuk.ui.feat_baca_kata.menu.MenuBacaKataActivity
import com.nara.bacayuk.ui.feat_baca_kata.quiz.QuizMenuActivity
import com.nara.bacayuk.ui.feat_riwayat.menu.MenuRiwayatActivity
import com.nara.bacayuk.ui.feat_student.list_student.ListStudentActivity
import com.nara.bacayuk.utils.*
import com.nara.bacayuk.writing.number.menu.MenuNumberActivity
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModel()
    private var balloon: Balloon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.apply {
            toolbar.txtTitle.text = "Menu Utama"
            toolbar.imgActionRight.setOnClickListener {
                showTip()
            }
            toolbar.imageView.setOnClickListener {
                onBackPressed()
            }
            val user: User? = mainViewModel.getUserDataStore()
            user?.uuid?.let { mainViewModel.getUser(it) }
            txtName.text = student?.fullName ?: "Guest"
            btnBacaHuruf.setOnClickListener {
                val intent = Intent(this@MainActivity, MenuBacaHurufActivity::class.java).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
            }

            btnBacaKata.setOnClickListener {
                val intent = Intent(this@MainActivity, MenuBacaKataActivity::class.java).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
            }

            btnRiwayat.setOnClickListener {
                val intent = Intent(this@MainActivity, MenuRiwayatActivity::class.java).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
//                playAudioFromUrl("https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20A.m4a?alt=media&token=499b04e1-e499-4279-ac82-89d085e36a44&_gl=1*mwt6xu*_ga*ODA4NDUxMTMwLjE2NDc4NzU2MDc.*_ga_CW55HF8NVT*MTY4NjY1NDc3My40My4xLjE2ODY2NTQ4MDcuMC4wLjA.")
            }

            btnBacaKalimat.setOnClickListener {
                val intent = Intent(
                    this@MainActivity,
                    QuizMenuActivity::class.java
                ).apply {
                    putExtra("student", student)
                    putExtra("isKata", false)
                }
                startActivity(intent)
            }

            btnTulisAngka.setOnClickListener {
                val intent = Intent(
                    this@MainActivity,
                    MenuNumberActivity::class.java
                ).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
            }
        }

        mainViewModel.user.observe(this@MainActivity) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d("LoginActivity", "onCreate: ${student?.isReadyHurufDataSet}")
                    if (student?.isReadyHurufDataSet == false) {
                        response.data.uuid?.let {
                            mainViewModel.createReportHurufDataSets(
                                true,
                                it, student.uuid,
                                student
                            )
//                            mainViewModel.createReportAngkaDataSets(
//                                true,
//                                it, student.uuid,
//                                student
//                            )
                        }
                    }
                }
                is Response.Error -> {
                    Toast.makeText(this, response.e, Toast.LENGTH_SHORT).show()
                    Log.d("LoginActivity", "onCreate: ${response.message}")
                }
                else -> {

                }
            }
        }
    }

    private fun showTip() {
        val tooltip: SimpleTooltip = SimpleTooltip.Builder(this)
            .anchorView(binding.toolbar.imgActionRight)
            .gravity(Gravity.BOTTOM)
            .dismissOnOutsideTouch(true)
            .dismissOnInsideTouch(false)
            .modal(false)
            .animated(false)
            .showArrow(false)
            .margin(16f)
            .backgroundColor(resources.getColor(R.color.white))
            .contentView(com.nara.bacayuk.R.layout.layout_action_menu)
            .focusable(true)
            .build()
        tooltip.show()

        val editSiswaText: TextView = tooltip?.findViewById(R.id.txt_edit_menu)!!

        editSiswaText.setText("Ganti Siswa")
        val deleteSiswaText: TextView =
            tooltip.findViewById(R.id.txt_delete_menu)!!
        deleteSiswaText.setText("Keluar")


        editSiswaText.setOnClickListener {
            val intent = Intent(this@MainActivity, ListStudentActivity::class.java)
            startActivity(intent)
        }

        deleteSiswaText.setOnClickListener {
            //todo: show log out dialog
            val dialog = ConfirmationDialogRedStyle(
                this@MainActivity,
                icon = R.drawable.ic_baseline_exit_to_app_24,
                title = "Konfirmasi Keluar",
                message = "Apakah anda yakin ingin keluar akun ?",
                onConfirmClickListener = {
                    mainViewModel.logOutUser()
                    openActivity(this@MainActivity, LoginActivity::class.java)
                    finish()
                }
            )
            dialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun showBalloon() {

        val height =  120
//        val height = if (isSelected) 175 else 105
        balloon = createBalloon(this@MainActivity) {
            setArrowSize(10)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(height)
            setArrowPosition(0.8f)
            setCornerRadius(4f)
            setAlpha(0.9f)
            setPaddingHorizontal(8)
            setPaddingVertical(4)
            setBackgroundColorResource(R.color.white)
            setMarginHorizontal(24)
            setLayout(R.layout.layout_action_menu)
            setTextColorResource(R.color.white)
            setTextIsHtml(true)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)

            balloon?.showAlignBottom(binding.toolbar.imgActionRight)
            Handler(Looper.getMainLooper()).postDelayed({ balloon?.dismiss() }, 2000)
        }
    }

    private fun cekUser() {
        val user: User? = mainViewModel.getUserDataStore()

        if (user == null || user.email == "") {
            openActivity(this@MainActivity, LoginActivity::class.java)
        } else {
//            user.apply {
//                Toast.makeText(this@MainActivity, uuid, Toast.LENGTH_SHORT).show()
//            }
        }
    }
}