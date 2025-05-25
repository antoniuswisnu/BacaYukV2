package com.nara.bacayuk.writing.quiz.result

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.writing.quiz.tracing.QuizAttemptDetail
import androidx.core.graphics.toColorInt

class ResultAdapter(private val attemptDetails: List<QuizAttemptDetail>) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestionNumber: TextView = view.findViewById(R.id.tv_question_number)
        val tvQuestionText: TextView = view.findViewById(R.id.tv_question_text)
        val tvYourAnswer: TextView = view.findViewById(R.id.tv_your_answer)
        val tvCorrectAnswerLabel: TextView = view.findViewById(R.id.tv_correct_answer_label)
        val tvCorrectAnswer: TextView = view.findViewById(R.id.tv_correct_answer_value)
        val ivStatus: ImageView = view.findViewById(R.id.iv_status_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_result_detail, parent, false)
        return ResultViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val detail = attemptDetails[position]

        holder.tvQuestionNumber.text = "Soal ${position + 1}"
        holder.tvQuestionText.text = "Pertanyaan: ${detail.questionText}"
        holder.tvYourAnswer.text = "Jawaban Anda: ${detail.userAnswerPredicted.ifEmpty { "-" }}"

        if (detail.isCorrect) {
            holder.tvCorrectAnswerLabel.visibility = View.GONE
            holder.tvCorrectAnswer.visibility = View.GONE
            holder.ivStatus.setImageResource(R.drawable.ic_checklist)
            holder.tvYourAnswer.setTextColor("#4CAF50".toColorInt())
        } else {
            holder.tvCorrectAnswerLabel.visibility = View.VISIBLE
            holder.tvCorrectAnswer.visibility = View.VISIBLE
            holder.tvCorrectAnswer.text = detail.correctAnswer
            holder.ivStatus.setImageResource(R.drawable.ic_wrong_answer)
            holder.tvYourAnswer.setTextColor("#F44336".toColorInt())
        }
    }

    override fun getItemCount() = attemptDetails.size
}
