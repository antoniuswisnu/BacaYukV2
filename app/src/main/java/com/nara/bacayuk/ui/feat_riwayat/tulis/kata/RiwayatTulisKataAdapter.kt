package com.nara.bacayuk.ui.feat_riwayat.tulis.kata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemRiwayatTulisKataBinding

class RiwayatTulisKataAdapter : RecyclerView.Adapter<RiwayatTulisKataAdapter.RecentAdapterViewHolder>(){

    inner class RecentAdapterViewHolder(val view: View) :
        RecyclerView.ViewHolder(view)

    private val diffCallback = object : DiffUtil.ItemCallback<Tulis>() {
        override fun areItemsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(list: ArrayList<Tulis>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentAdapterViewHolder {
        val binding = ItemRiwayatTulisKataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int
    ) {
        holder.view.apply {
            val data = differ.currentList[position]
            val report = data.reportTulisKata

            val binding = ItemRiwayatTulisKataBinding.bind(this)
            binding.textKata.text = data.tulisKata

            val materiKataFinished = report?.materiTulisKata == true
            val latihanKataFinished = report?.latihanTulisKata == true

            binding.apply {
                if (materiKataFinished) {
                    imgMateriTulisKata.setImageResource(R.drawable.ic_finished)
                } else {
                    imgMateriTulisKata.setImageResource(R.drawable.ic_unfinished)
                }
                if (latihanKataFinished) {
                    imgMateriTulisKata.setImageResource(R.drawable.ic_finished)
                } else {
                    imgMateriTulisKata.setImageResource(R.drawable.ic_unfinished)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}