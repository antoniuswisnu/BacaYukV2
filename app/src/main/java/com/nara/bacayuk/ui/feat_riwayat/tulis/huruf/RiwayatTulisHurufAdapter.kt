package com.nara.bacayuk.ui.feat_riwayat.tulis.huruf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemRiwayatTulisHurufBinding

class RiwayatTulisHurufAdapter :
        RecyclerView.Adapter<RiwayatTulisHurufAdapter.RecentAdapterViewHolder>(){
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
        val binding = ItemRiwayatTulisHurufBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int
    ) {
        holder.view.apply {
            val data = differ.currentList[position]
            val report = data.reportTulisHuruf

            val binding = ItemRiwayatTulisHurufBinding.bind(this)
            binding.textTulisHuruf.text = data.tulisHuruf

            val materiKapitalFinished = report?.materiTulisHurufKapital == true
            val materiNonKapitalFinished = report?.materiTulisHurufKapital == true
            val latihanKapitalFinished = report?.latihanTulisHurufKapital == true
            val latihanNonKapitalFinished = report?.latihanTulisHurufNonKapital == true

            binding.apply {
                if (materiKapitalFinished) {
                    imgMateriTulisHurufKapital.setImageResource(R.drawable.ic_finished)
                } else {
                    imgMateriTulisHurufKapital.setImageResource(R.drawable.ic_unfinished)
                }

                if (materiNonKapitalFinished) {
                    imgMateriTulisHurufNonkapital.setImageResource(R.drawable.ic_finished)
                } else {
                    imgMateriTulisHurufNonkapital.setImageResource(R.drawable.ic_unfinished)
                }

                if (latihanKapitalFinished) {
                    imgLatihanTulisHurufKapital.setImageResource(R.drawable.ic_finished)
                } else {
                    imgLatihanTulisHurufKapital.setImageResource(R.drawable.ic_unfinished)
                }

                if (latihanNonKapitalFinished) {
                    imgLatihanTulisHurufNonkapital.setImageResource(R.drawable.ic_finished)
                } else {
                    imgLatihanTulisHurufNonkapital.setImageResource(R.drawable.ic_unfinished)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}