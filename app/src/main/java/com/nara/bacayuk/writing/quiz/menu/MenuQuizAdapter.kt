package com.example.tracingalphabet.quiz.menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracingalphabet.databinding.ItemQuizSetBinding

class MenuQuizAdapter(
    private val onMenuQuizClick: (MenuQuiz) -> Unit,
    private val onDeleteClick: (MenuQuiz) -> Unit,
    private val onEditClick: (MenuQuiz) -> Unit
) : ListAdapter<MenuQuiz, MenuQuizAdapter.MenuQuizViewHolder>(MenuQuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuQuizViewHolder {
        val binding = ItemQuizSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuQuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuQuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MenuQuizViewHolder(
        private val binding: ItemQuizSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(quizSet: MenuQuiz) {
            binding.tvTitle.text = quizSet.title
            binding.tvDescription.text = quizSet.description
//            binding.tvQuestionCount.text = "${quizSet.questionCount} Soal"
            binding.btnEdit.setOnClickListener {
                onEditClick(quizSet)
            }
            binding.root.setOnClickListener {
                onMenuQuizClick(quizSet)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(quizSet)
            }
        }
    }

    class MenuQuizDiffCallback : DiffUtil.ItemCallback<MenuQuiz>() {
        override fun areItemsTheSame(oldItem: MenuQuiz, newItem: MenuQuiz): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MenuQuiz, newItem: MenuQuiz): Boolean {
            return oldItem == newItem
        }
    }
}