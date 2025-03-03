package com.example.tracingalphabet.quiz.question

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracingalphabet.databinding.ItemQuestionBinding

class QuestionAdapter(
    private val onDeleteClick: (Question) -> Unit
) : ListAdapter<Question, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuestionViewHolder(
        private val binding: ItemQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Question) {
            binding.tvQuestion.text = quiz.question
            binding.tvQuestionType.text = quiz.questionType
            binding.btnDelete.setOnClickListener {
                onDeleteClick(quiz)
            }
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, AddEditQuestionActivity::class.java)
                intent.putExtra("quizSetId", quiz.id)
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