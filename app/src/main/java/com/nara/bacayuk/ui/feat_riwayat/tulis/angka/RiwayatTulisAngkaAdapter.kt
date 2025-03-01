package com.nara.bacayuk.ui.feat_riwayat.tulis.angka

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemRiwayatTulisAngkaBinding

class RiwayatTulisAngkaAdapter :
    RecyclerView.Adapter<RiwayatTulisAngkaAdapter.RecentAdapterViewHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapterViewHolder {
        val binding =
            ItemRiwayatTulisAngkaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int) {
        holder.view.apply {
            val data = differ.currentList[position]
            val report = data.reportTulisAngka
            Log.d("TAG", "onBindViewHolder: $data")
            val binding = ItemRiwayatTulisAngkaBinding.bind(this)
            binding.txtAngka.text = data.tulisAngka
//            binding.imgChecklist.invisible()
            binding.apply {
                imgMateriTulisAngka.setImageDrawable(
                     ContextCompat.getDrawable(
                        context,
                        if (report?.materiAngka == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                )

                imgLatihanTulisAngka.setImageDrawable((
                     ContextCompat.getDrawable(
                        context,
                        if (report?.latihanAngka == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                        ))
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}

