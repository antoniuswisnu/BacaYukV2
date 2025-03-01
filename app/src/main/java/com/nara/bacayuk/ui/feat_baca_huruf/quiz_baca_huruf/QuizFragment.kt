package com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.databinding.FragmentQuizBinding
import com.nara.bacayuk.ui.customview.AnswerStatusDialog
import com.nara.bacayuk.ui.customview.OnDismissDialog
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufActivity
import com.nara.bacayuk.utils.playAudioFromRawAssetsFileString
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val ARG_PARAM1 = "param1"

class QuizFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: (CharSequence) -> Unit
    private var abjad: Abjad? = null

    private lateinit var wrongAnswerNonKapital: List<String>
    private var correctAnswerNonKapital: List<String>  = listOf()
    private var wrongAndCorrectAnswerNonKapital : List<String>  = listOf()

    private lateinit var wrongAnswerKapital: List<String>
    private var correctAnswerKapital: List<String>  = listOf()
    private var wrongAndCorrectAnswerKapital : List<String>  = listOf()

    private val quizBacaHurufViewModel: QuizBacaHurufViewModel by viewModel()

    private var isKapital: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    fun mergeAndShuffle(list1: List<String>, list2: List<String>): List<String> {
        val mergedList = list1 + list2
        return mergedList.shuffled()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

//        Toast.makeText(context, "Correct answer: $correctAnswer", Toast.LENGTH_SHORT).show()

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.quiz.apply {

            abjad = MateriBacaHurufActivity.dataAbjad
            wrongAnswerNonKapital = getTwoRandomAbjadNonKapital(abjad?.abjadNonKapital ?: "-")
            wrongAnswerKapital = getTwoRandomAbjadKapital(abjad?.abjadKapital ?: "-")
            correctAnswerNonKapital = listOf<String>(abjad?.abjadNonKapital ?: "-")
            wrongAndCorrectAnswerNonKapital = mergeAndShuffle(wrongAnswerNonKapital, correctAnswerNonKapital)

            correctAnswerKapital = listOf<String>(abjad?.abjadKapital ?: "-")
            wrongAndCorrectAnswerKapital = mergeAndShuffle(wrongAnswerKapital, correctAnswerKapital)

            txtAbjadQuiz.setOnTouchListener(ChoiceTouchListener())

            txtOpt1.setOnDragListener(ChoiceDragListener())
            txtOpt2.setOnDragListener(ChoiceDragListener())
            txtOpt3.setOnDragListener(ChoiceDragListener())
            when (param1) {
                "0" -> {
                    playAudioFromRawAssetsFileString(requireContext(),"ins_arahkan_huruf")
                    reset(false)
                    txtAbjadQuiz.text = abjad?.abjadNonKapital
                    isKapital = false
//
                    MaterialIntroView.Builder(requireActivity())
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.ALL)
                        .setDelayMillis(500)
                        .enableFadeAnimation(true)
                        .performClick(true)
                        .setInfoText("Arahkah huruf abjad ini menuju huruf yang benar dengan cara menekan, tahan, lalu geser")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(binding.quiz.txtAbjadQuiz)
                        .setUsageId("intro_abjad") //THIS SHOULD BE UNIQUE ID
                        .show()
                }
                "1" -> {
                    playAudioFromRawAssetsFileString(requireContext(),"ins_arahkan_huruf")
                    isKapital = true
                    reset(true)
                    txtAbjadQuiz.text = abjad?.abjadKapital

                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, listener: (CharSequence) -> Unit) =
            QuizFragment().apply {
                this.listener = listener
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    private class ChoiceTouchListener : View.OnTouchListener {
        @SuppressLint("NewApi")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
                 * - clip data could be set to pass data as part of drag
                 * - shadow can be tailored
                 */
                //change the text color to teal 200
                val tv = view as TextView
                tv.setTextColor(view.context.getColor(R.color.teal_600))
                //change size of text only dragged
//                tv.textSize = 200f
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0)
                true
            } else {
                false
            }
        }
    }

    fun reset(isKapital: Boolean) {
        binding.quiz.apply {

            Log.d("cekdata", "correctAnswerKapital: ${wrongAndCorrectAnswerKapital[2]}")
            txtAbjadQuiz.visibility = View.VISIBLE

            txtOpt1.text = if (isKapital) wrongAndCorrectAnswerKapital[0] else wrongAndCorrectAnswerNonKapital[0]
            txtOpt2.text = if (isKapital) wrongAndCorrectAnswerKapital[1] else wrongAndCorrectAnswerNonKapital[1]
            txtOpt3.text = if (isKapital) wrongAndCorrectAnswerKapital[2] else wrongAndCorrectAnswerNonKapital[2]
            txtOpt1.tag = null
            txtOpt2.tag = null
            txtOpt3.tag = null
            txtOpt1.typeface = Typeface.DEFAULT
            txtOpt2.typeface = Typeface.DEFAULT
            txtOpt3.typeface = Typeface.DEFAULT
            txtOpt1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutral_300
                )
            )

            txtOpt2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutral_300
                )
            )
            txtOpt3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutral_300
                )
            )
            txtOpt1.setOnDragListener(ChoiceDragListener())
            txtOpt2.setOnDragListener(ChoiceDragListener())
            txtOpt3.setOnDragListener(ChoiceDragListener())
        }
    }

    inner class ChoiceDragListener : View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val view = event.localState as View
                    val dropTarget = v as TextView
                    val dropped = view as TextView

                    if (dropTarget.text.toString()[0] == dropped.text.toString()[0]) {
                        view.setVisibility(View.INVISIBLE)
                        dropTarget.text = dropped.text
                        dropTarget.setTextColor(resources.getColor(R.color.teal_600))
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD)
                        val tag = dropTarget.tag
                        if (tag != null) {
                            val existingID = tag as Int
                            binding.root.findViewById<View>(existingID).setVisibility(View.VISIBLE)
                        }
                        dropTarget.tag = dropped.id
                        dropTarget.setOnDragListener(null)
                        val user: User? = quizBacaHurufViewModel.getUserDataStore()
                        val dialog = AnswerStatusDialog(
                            v.context,
                            icon = R.drawable.ic_checklist,
                             status =  "Benar",
                            object: OnDismissDialog {
                                override fun onDismissDialog() {

                                }
                            }
                        )
                        dialog.show()
                        val layoutParams = WindowManager.LayoutParams()
                        layoutParams.copyFrom(dialog.window?.attributes)
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                        dialog.getWindow()?.setAttributes(layoutParams)
                        if (isKapital) {
                            abjad?.reportHuruf?.quizHurufKapital = true
                        } else abjad?.reportHuruf?.quizHurufNonKapital = true
                        val reportHuruf = abjad?.reportHuruf
                        quizBacaHurufViewModel.updateReportHuruf(user?.uuid?: "-",
                            QuizBacaHurufActivity.student?.uuid ?: "-",
                            reportHuruf ?: ReportHuruf()
                        )
                        reset(isKapital)
                    } else {
                        val dialog = AnswerStatusDialog(
                            v.context,
                            icon = R.drawable.ic_wrong_answer,
                            status =  "Salah",
                            object : OnDismissDialog {
                                override fun onDismissDialog() {

                                }
                            }
                        )
                        dialog.show()
                        val layoutParams = WindowManager.LayoutParams()
                        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                        dialog.getWindow()?.setAttributes(layoutParams)
                        reset(isKapital)
                    }


                }
                DragEvent.ACTION_DRAG_ENDED -> {}
                else -> {}
            }
            return true
        }
    }
}