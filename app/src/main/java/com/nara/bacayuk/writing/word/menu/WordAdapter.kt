package com.nara.bacayuk.writing.word.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemAbjadMenuBinding
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.visible

class WordAdapter(
    private val listener: AdapterListener,
    private val longClickListener: (ReportTulisKata) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(val binding: ItemAbjadMenuBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Tulis>() {
        override fun areItemsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(list: ArrayList<Tulis>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemAbjadMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val dataTulis = differ.currentList[position]
        holder.binding.apply {
            txtAbjad.text = dataTulis?.tulisKata
            imgChecklist.invisible()
            dataTulis?.reportTulisKata?.let { report ->
                if (report.materiTulisKata && report.latihanTulisKata) {
                    imgChecklist.visible()
                } else {
                    imgChecklist.invisible()
                }
            }

            root.setOnClickListener {
                listener.onClick(dataTulis, position, holder.itemView, "word_trace")
            }

            root.setOnLongClickListener {
                dataTulis?.reportTulisKata?.let { report ->
                    longClickListener.invoke(report)
                }
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
