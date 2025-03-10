package com.nara.bacayuk.ui.feat_riwayat.menu

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMenuRiwayatBinding
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatHurufActivity
import com.nara.bacayuk.ui.feat_riwayat.kalimat.RiwayatKalimatActivity
import com.nara.bacayuk.ui.feat_riwayat.kata.RiwayatKataActivity
import com.nara.bacayuk.ui.feat_riwayat.tulis.angka.RiwayatTulisAngkaActivity
import com.nara.bacayuk.ui.feat_riwayat.tulis.huruf.RiwayatTulisHurufActivity
import com.nara.bacayuk.ui.feat_riwayat.tulis.kata.RiwayatTulisKataActivity
import com.nara.bacayuk.utils.invisible

class MenuRiwayatActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuRiwayatBinding.inflate(layoutInflater) }
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        binding.apply {
            textView.text = student?.fullName
            "${student?.kelas ?: "-"} - ${student?.asalSekolah ?: "-"}".also { txtDesc.text = it }
            toolbar.txtTitle.text = "Riwayat Belajar"
            toolbar.imageView.setOnClickListener { onBackPressed() }
            toolbar.imgActionRight.invisible()
            btnHuruf.setOnClickListener {
                val intent =
                    Intent(this@MenuRiwayatActivity, RiwayatHurufActivity::class.java).apply {
                        putExtra("student", student)
                    }
                startActivity(intent)
            }

            btnKata.setOnClickListener {
                val intent =
                    Intent(this@MenuRiwayatActivity, RiwayatKataActivity::class.java).apply {
                        putExtra("student", student)
                    }
                startActivity(intent)
            }

            btnKalimat.setOnClickListener {
                val intent =
                    Intent(this@MenuRiwayatActivity, RiwayatKalimatActivity::class.java).apply {
                        putExtra("student", student)
                    }
                startActivity(intent)
            }

            btnTulisAngka.setOnClickListener {
                val intent = Intent(this@MenuRiwayatActivity, RiwayatTulisAngkaActivity::class.java).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
            }

            btnTulisHuruf.setOnClickListener {
                val intent = Intent(this@MenuRiwayatActivity, RiwayatTulisHurufActivity::class.java).apply {
                    putExtra("student", student)
                }
                startActivity(intent)
            }

            btnTulisKata.setOnClickListener {
                val intent =
                    Intent(this@MenuRiwayatActivity, RiwayatTulisKataActivity::class.java).apply {
                        putExtra("student", student)
                    }
                startActivity(intent)
            }

//            btnTulisKuis.setOnClickListener {
//                val intent = Intent(this@MenuRiwayatActivity, RiwayatTulisKuisActivity::class.java).apply {
//                    putExtra("student", student)
//                }
//                startActivity(intent)
//            }
        }
    }
}