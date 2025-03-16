package com.nara.bacayuk.ui.feat_student.list_student

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityListStudentBinding
import com.nara.bacayuk.ui.custom_view.ConfirmationDialogRedStyle
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_auth.register.RegisterActivity
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.ui.feat_menu_utama.MainViewModel
import com.nara.bacayuk.ui.feat_student.add_edit_student.AddEditStudentActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.DATA
import com.nara.bacayuk.utils.EDIT
import com.nara.bacayuk.utils.MESSAGE_HURUF_SUCCESS
import com.nara.bacayuk.utils.MESSAGE_KALIMAT_SUCCESS
import com.nara.bacayuk.utils.MESSAGE_KATA_SUCCESS
import com.nara.bacayuk.utils.gone
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.openActivity
import com.nara.bacayuk.utils.visible
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListStudentActivity : AppCompatActivity(), AdapterListener {
    private val binding: ActivityListStudentBinding by lazy {
        ActivityListStudentBinding.inflate(layoutInflater)
    }
    private var balloon: Balloon? = null
    private val studentAdapter by lazy { StudentAdapter(this@ListStudentActivity) }
    private var selectedStudent: Student? = null
    private val progressDialog by lazy { ProgressDialog(this) }
    private val listStudentViewModel: ListStudentViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val user: User? = listStudentViewModel.getUserDataStore()
        if (user == null || user.email == "") {
            openActivity(this@ListStudentActivity, RegisterActivity::class.java)
        }
        mainViewModel.statusCreateData.observe(this@ListStudentActivity) {
            if (it.size > 1) {
                progressDialog.setCancelable(false)
                progressDialog.setMessage("${it[0]}\n${it[1]}\n${it[2]}")
                progressDialog.show()
                if (it[0] == MESSAGE_HURUF_SUCCESS && it[1] == MESSAGE_KATA_SUCCESS && it[2] == MESSAGE_KALIMAT_SUCCESS){
                    progressDialog.dismiss()
                    val intent = Intent(this@ListStudentActivity, MainActivity::class.java).apply {
                        putExtra("student", selectedStudent)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

        listStudentViewModel.students.observe(this@ListStudentActivity) { response ->
            dialog.dismiss()
            when (response) {
                is Response.Success -> {
                    if (response.data.isEmpty()){
                        handleEmptyState(true)
                    } else {
                        handleEmptyState(false)
                        val listData = response.data.sortedBy { it.fullName }
                        studentAdapter.submitData(listData)
                        response.data.forEach {
                            Log.d("liststudent", "${it.fullName} - ${it.uuid}")
                        }
                    }
                }
                is Response.Error -> {
                    Toast.makeText(this@ListStudentActivity, response.message, Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }

        binding.apply {
            toolbar.apply {
                txtTitle.text = getString(com.nara.bacayuk.R.string.pilih_siswa)
                imageView.invisible()
                imgActionRight.setOnClickListener {
                    if (selectedStudent != null) {
                        showTip(true)
                    } else {
                        showTip(false)
                    }
                }
            }

            layoutStudent.rvListStudent.apply {
                adapter = this@ListStudentActivity.studentAdapter
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@ListStudentActivity, 4)
            }

            binding.layoutStudent.btnSelectStudent.isEnabled = selectedStudent!= null

            layoutStudent.btnSelectStudent.setOnClickListener {
                if (selectedStudent?.isReadyHurufDataSet == false ) {
                    mainViewModel.createReportHurufDataSets(
                        true,
                        user?.uuid?: "-", selectedStudent!!.uuid,
                        selectedStudent!!
                    )
                } else {
                    val intent = Intent(this@ListStudentActivity, MainActivity::class.java).apply {
                        putExtra("student", selectedStudent)
                    }
                    startActivity(intent)
                    finish()
                }

            }

            layoutEmpty.btnSelect.setOnClickListener {
                openActivity(this@ListStudentActivity, AddEditStudentActivity::class.java)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val uidUser = listStudentViewModel.getUID() ?: "-"
        Log.d("liststudent", "onResume: $uidUser")
        listStudentViewModel.getAllStudent(uidUser).also {
            dialog.show()
        }
        progressDialog.dismiss()
    }

    private fun showTip(isSelected: Boolean = false){
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
            .contentView(R.layout.layout_action_siswa)
            .focusable(true)
            .build()
        tooltip.show()

        val editSiswaText: TextView = tooltip.findViewById(R.id.txt_edit_siswa)!!
        val addSiswaText: TextView = tooltip.findViewById(R.id.txt_add_siswa)!!
        val deleteSiswaText: TextView = tooltip.findViewById(R.id.txt_delete_siswa)!!
        addSiswaText.setOnClickListener {
            openActivity(this@ListStudentActivity, AddEditStudentActivity::class.java)
        }

        editSiswaText.setOnClickListener {
            val intent = Intent(this@ListStudentActivity, AddEditStudentActivity::class.java)
                .apply {
                    putExtra(DATA, selectedStudent)
                    putExtra(EDIT, true)

                }
            startActivity(intent)
        }

        deleteSiswaText.setOnClickListener {
            val dialogDelete = ConfirmationDialogRedStyle(
                this@ListStudentActivity,
                icon = com.nara.bacayuk.R.drawable.ic_baseline_delete_24,
                title = "Apakah Anda yakin akan menghapus profil siswa ini?",
                message = "Profil siswa akan dihapus permanen",
                onConfirmClickListener = {
                    val uidUser = listStudentViewModel.getUID() ?: "-"
                    selectedStudent?.uuid?.let { it1 ->
                        listStudentViewModel.deleteStudentFirestore(uidUser,
                            it1
                        ).also {
                            dialog.show()
                        }
                    }
                    onResume()
                }
            )
            dialogDelete.show()
        }
        if (isSelected) {
            editSiswaText.visible()
            deleteSiswaText.visible()
            addSiswaText.visible()
        } else {
            editSiswaText.gone()
            deleteSiswaText.gone()
            addSiswaText.visible()
        }
    }

    private fun showBalloon(isSelected: Boolean = false) {
        val height = if (isSelected) 140 else 70
        balloon = createBalloon(this@ListStudentActivity) {
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
            setLayout(R.layout.layout_action_siswa)
            setTextColorResource(R.color.white)
            setTextIsHtml(true)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)

            balloon?.showAlignBottom(binding.toolbar.imgActionRight)
            Handler(Looper.getMainLooper()).postDelayed({ balloon?.dismiss() }, 2000)
        }
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.layoutEmpty.root.visible()
            binding.layoutStudent.root.gone()
        } else {
            binding.layoutEmpty.root.gone()
            binding.layoutStudent.root.visible()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            finishAffinity()
            return
        } else {
            backToast = Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT)
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        selectedStudent = data as Student?
        binding.layoutStudent.btnSelectStudent.isEnabled = selectedStudent!= null
    }
}