package com.nara.bacayuk.ui.feat_belajar_kalimat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportKalimat
import com.nara.bacayuk.data.model.ReportKata
import com.nara.bacayuk.data.model.SoalKata
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizKalimatBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.CenterLinearLayoutManager
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import com.nara.bacayuk.ui.feat_baca_kata.quiz.QuizSusunAdapter
import com.nara.bacayuk.ui.feat_baca_kata.quiz.QuizViewModel
import com.nara.bacayuk.ui.listener.adapter.AdapterQuizListener
import com.nara.bacayuk.ui.listener.adapter.ViewPositionListener
import com.nara.bacayuk.utils.gone
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.loadImage
import com.nara.bacayuk.utils.playAudioFromRawAssetsFileString
import com.nara.bacayuk.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizKalimatActivity : AppCompatActivity(), AdapterQuizListener, ViewPositionListener {
    private val binding by lazy { ActivityQuizKalimatBinding.inflate(layoutInflater) }
    var student: Student? = null
    var soalKata: SoalKata? = null
    var listQuestions: MutableList<String> = mutableListOf()
    var listAnswer: MutableList<String> = mutableListOf()
    var sizeQuestion = 0
    var count = 0
    var lastOptionView: View? = null
    var lastAnswerView: View? = null
    var isKata: Boolean = false
    var reportKata: ReportKata? = null
    var reportKalimat: ReportKalimat? = null
    private val quizViewModel: QuizViewModel by viewModel()
    private val adapterOption by lazy {
        QuizSusunAdapter(
            this@QuizKalimatActivity,
            this@QuizKalimatActivity,
            "option"
        )
    }
    private val adapterAnswer by lazy {
        QuizSusunAdapter(
            this@QuizKalimatActivity,
            this@QuizKalimatActivity,
            "answer"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isKata = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getBooleanExtra("isKata", false)
        } else {
            intent.getBooleanExtra("isKata", false)
        }

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        soalKata = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("quiz", SoalKata::class.java)
        } else {
            intent.getParcelableExtra("quiz") as SoalKata?
        }

        if (isKata) {
            reportKata = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("data", ReportKata::class.java)
            } else {
                intent.getParcelableExtra("data") as ReportKata?
            }
        } else {
            reportKalimat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("data", ReportKalimat::class.java)
            } else {
                intent.getParcelableExtra("data") as ReportKalimat?
            }
        }

        listQuestions = stringToList(soalKata?.optionList ?: "", "-")
        listQuestions.shuffle()
        sizeQuestion = listQuestions.size

        binding.apply {
            binding.toolbar.apply {
                txtTitle.text = if (isKata) "Baca Kata" else "Baca Kalimat"
                imageView.setOnClickListener { onBackPressed() }
                imgActionRight.invisible()
            }
            if (isKata){
                playAudioFromRawAssetsFileString(this@QuizKalimatActivity,"ins_susun_kata")
                textView2.text = "Susunlah pilihan suku kata berikut menjadi kata yang benar!"
            }else {
                playAudioFromRawAssetsFileString(this@QuizKalimatActivity,"ins_susun_kalimat")
            }

            binding.txtAnswer.setOnClickListener {
                if (listAnswer.isNotEmpty()) {
                    val lastItem = listAnswer.removeAt(listAnswer.size - 1) // Hapus item terakhir dari listAnswer
                    when (lastItem) {
                        listQuestions[0] -> binding.opta.opt1.visible()
                        listQuestions[1] -> binding.opti.opt1.visible()
                        listQuestions[2] -> binding.optu.opt1.visible()
                        // ... tambahkan kasus lain jika ada lebih banyak opsi
                    }

                    // Update tampilan txtAnswer dengan item yang tersisa di listAnswer
                    if (listAnswer.isEmpty()) {
                        binding.txtAnswer.text = "----"
                    } else {
                        binding.txtAnswer.text = listAnswer.joinToString(if (isKata) "" else " ")
                    }
                }
            }

            imageView4.loadImage(this@QuizKalimatActivity, soalKata?.imageUrl ?: "")
            opta.opt1.apply {
                text = listQuestions[0]
                setOnClickListener {
                    opta.opt1.invisible()
                    listAnswer.add(listQuestions[0])
                    binding.btnLogin.isEnabled = true
                    binding.txtAnswer.text = listAnswer.joinToString(if (isKata) "" else " ")
                }
            }
            opti.opt1.apply {
                text = listQuestions[1]
                setOnClickListener {
                    opti.opt1.invisible()
                    listAnswer.add(listQuestions[1])
                    binding.btnLogin.isEnabled = true
                    binding.txtAnswer.text = listAnswer.joinToString(if (isKata) "" else " ")
                }
            }

            optu.opt1.apply {
                text = listQuestions[2]
                setOnClickListener {
                    optu.opt1.invisible()
                    listAnswer.add(listQuestions[2])
                    binding.btnLogin.isEnabled = true
                    binding.txtAnswer.text = listAnswer.joinToString(if (isKata) "" else " ")
                }
            }

            if (isKata) {
                if ((soalKata?.level ?: 0) < 8) {
                    opte.root.gone()
                } else {
                    opte.root.visible()
                    opte.opt1.text = listQuestions[3]
                    opte.opt1.apply {
                        text = listQuestions[3]
                        setOnClickListener {
                            optu.opt1.invisible()
                            listAnswer.add(listQuestions[3])
                            binding.txtAnswer.text =
                                listAnswer.joinToString(if (isKata) "" else " ")
                        }
                    }
                }
            } else {
                opte.root.gone()
            }


            rvOption.apply {
                adapter = adapterOption
                layoutManager = CenterLinearLayoutManager(
                    this@QuizKalimatActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                clipToPadding = false
            }
            rvAnswer.apply {
                adapter = adapterAnswer
                layoutManager = LinearLayoutManager(
                    this@QuizKalimatActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

            btnLogin.setOnClickListener {
                Log.d("quizsusun", "$isKata- $reportKata- $reportKalimat- ${student?.uuid ?: "-"}")
                if (txtAnswer.text == soalKata?.correctAnswer) {
                    if (reportKata != null) {
                        for (item in reportKata!!.quizSusunKata) {
                            if (item.level == soalKata!!.level) {
                                item.isCorrect = true
                                val index = item.level - 1
                                reportKata!!.quizSusunKata[index] = item
                                quizViewModel.updateReportKata(
                                    student?.uuid ?: "-", reportKata!!
                                )
                            }
                        }
                    }

                    if (reportKalimat != null) {
                        for (item in reportKalimat!!.quizSusunKata) {
                            if (item.level == soalKata!!.level) {
                                item.isCorrect = true
                                val index = item.level - 1
                                reportKalimat!!.quizSusunKata[index] = item
                                quizViewModel.updateReportKalimat(
                                    student?.uuid ?: "-", reportKalimat!!
                                )
                            }
                        }
                    }
                    val dialog = AnswerStatusDialog(
                        this@QuizKalimatActivity,
                        icon = R.drawable.ic_checklist,
                        status = "Benar",
                        object: OnDismissDialog {
                            override fun onDismissDialog() {
                                onBackPressed()
                            }
                        }
                    )
                    dialog.show()
                    val layoutParams = WindowManager.LayoutParams()
                    layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    dialog.getWindow()?.setAttributes(layoutParams)
                } else {
                    val dialog = AnswerStatusDialog(
                        this@QuizKalimatActivity,
                        icon = R.drawable.ic_wrong_answer,
                        status = "Salah",
                        object : OnDismissDialog {
                            override fun onDismissDialog() {
                                resetView()
                            }
                        }
                    )
                    dialog.show()
                    val layoutParams = WindowManager.LayoutParams()
                    layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    dialog.getWindow()?.setAttributes(layoutParams)
                }
            }
        }
    }


    fun stringToList(str: String, separator: String): MutableList<String> {
        return str.split(separator).toMutableList()
    }

    fun resetView() {
        listAnswer.clear()
        binding.txtAnswer.text = "----"
        binding.btnLogin.isEnabled = false
        binding.apply {
            opta.opt1.visible()
            opti.opt1.visible()
            optu.opt1.visible()
            if ((soalKata?.level ?: 0) < 8 && isKata) {
                opte.root.gone()
            } else {
                if (isKata){
                    opte.root.visible()
                } else {
                    opte.root.gone()
                }

            }
        }
    }

    override fun getView(view: View?, type: String) {
        when (type) {
            "option" -> {
                lastOptionView = view
            }
            "answer" -> {
                //getview first
                lastAnswerView = view
            }
        }
    }

    override fun onClick(data: Any?, position: Int?, view: View, view2: View, type: String) {
        when (type) {
            "option" -> {
//                moveView(view, binding.txtAnswer )
                view.invisible()
                listAnswer.add(data as String)

                binding.txtAnswer.animate().alpha(1.0f)
                    .setDuration(800)
                    .setStartDelay((50).toLong())
                    .withEndAction { binding.txtAnswer.text = listAnswer.joinToString("") }
                    .start()
            }
            "pilgan" -> {
            }
        }
    }
}