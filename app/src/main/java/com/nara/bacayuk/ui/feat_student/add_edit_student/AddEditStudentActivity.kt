package com.nara.bacayuk.ui.feat_student.add_edit_student

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityAddEditStudentBinding
import com.nara.bacayuk.ui.customview.waitingDialog
import com.nara.bacayuk.utils.DATA
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.showAddSuccessToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddEditStudentActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddEditStudentBinding.inflate(layoutInflater) }
    private val addEditStudentViewModel: AddEditStudentViewModel by viewModel()
    private var isEditStudent = false
    private var dataStudent: Student? = null
    //dialog
    private val dialog by lazy { waitingDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dataStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DATA, Student::class.java)
        } else {
            intent.getParcelableExtra(DATA) as Student?
        }

        binding.apply {
            toolbar.apply {
                imgActionRight.invisible()
                imageView.setOnClickListener { finish() }
                txtTitle.text = if (dataStudent!=null) "Edit Siswa" else "Tambah Siswa"
            }
            if (dataStudent!=null) {
                isEditStudent = true
                edtName.setText(dataStudent!!.fullName)
                edtKelas.setText(dataStudent!!.kelas)
                edtNoabsen.setText(dataStudent!!.noAbsen)
                edtAsalSekolah.setText(dataStudent!!.asalSekolah)
                edtTahunMasukSekolah.setText(dataStudent!!.tahunMasukSekolah)
            }

            btnSaveStudent.setOnClickListener {
                val name = edtName.text.toString()
                val kelas = edtKelas.text.toString()
                val noAbsen = edtNoabsen.text.toString()
                val asalSekolah = edtAsalSekolah.text.toString()
                val thMasukSekolah = edtTahunMasukSekolah.text.toString()
                //generate uid for new student
                val randomUid = UUID.randomUUID().toString()
                val uuid = if (isEditStudent) dataStudent?.uuid else randomUid

                if (name=="" || name.isEmpty()){
                    edtName.setError("Wajib diisi")
                }else {
                    val student = Student(
                        uuid = uuid ?: "-",
                        fullName = name,
                        kelas = kelas,
                        noAbsen = noAbsen,
                        asalSekolah = asalSekolah,
                        tahunMasukSekolah = thMasukSekolah,
                    )
                    val uidUser = addEditStudentViewModel.getUID() ?: "-"
                    addEditStudentViewModel.addUserToFirestore(uidUser, student).also {
                        dialog.show()
                    }
                }

            }

            addEditStudentViewModel.isSuccess.observe(this@AddEditStudentActivity) { response ->
                dialog.dismiss()
                when (response) {
                    is Response.Success<*> -> {
                        showAddSuccessToast(this@AddEditStudentActivity)
                        finish()

                    }
                    is Response.Error -> {
                        Toast.makeText(this@AddEditStudentActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}