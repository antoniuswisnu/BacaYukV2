package com.nara.bacayuk.writing.word.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemAbjadMenuBinding
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.visible

class WordAdapter(val listener: AdapterListener) : RecyclerView.Adapter<WordAdapter.RecentAdapterViewHolder>(){

    inner class RecentAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val diffCallback = object : DiffUtil.ItemCallback<Tulis>() {
        override fun areItemsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    private var typ = "-"

    fun submitData(list: ArrayList<Tulis>, type: String) {
        differ.submitList(list)
        typ = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapterViewHolder {
        val binding = ItemAbjadMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int) {
        holder.view.apply {
            val data = differ.currentList[position]
            val binding = ItemAbjadMenuBinding.bind(this)

            binding.txtAbjad.text = data?.tulisKata
            binding.imgChecklist.invisible()

            if (data?.reportTulisKata != null){
                if (data.reportTulisKata.materiTulisKata
                    && data.reportTulisKata.latihanTulisKata){
                    binding.imgChecklist.visible()
                } else {
                    binding.imgChecklist.invisible()
                }
            }

            rootView.setOnClickListener{
                listener.onClick(data, position, binding.root, "")
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}


