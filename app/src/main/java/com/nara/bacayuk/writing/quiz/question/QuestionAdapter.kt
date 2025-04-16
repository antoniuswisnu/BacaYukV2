package com.nara.bacayuk.writing.quiz.question

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.databinding.ItemQuizQuestionBinding

class QuestionAdapter(
    private val onDeleteClick: (Question) -> Unit
) : ListAdapter<Question, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuizQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            position = position
        )
    }

    inner class QuestionViewHolder(
        private val binding: ItemQuizQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(quiz: Question, position: Int) {
            binding.tvQuestion.text = "${position + 1}. ${quiz.question}"
            binding.btnDelete.setOnClickListener {
                onDeleteClick(quiz)
            }
            binding.btnEdit.setOnClickListener {
                val intent = Intent(binding.root.context, AddEditQuestionActivity::class.java)
                intent.putExtra("quizSetId", quiz.quizSetId)
                intent.putExtra("quiz", quiz)
                intent.putExtra("quizId", quiz.id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }
}