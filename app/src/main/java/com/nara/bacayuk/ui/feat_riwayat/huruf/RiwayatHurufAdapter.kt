package com.nara.bacayuk.ui.feat_riwayat.huruf

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Abjad
import com.nara.bacayuk.databinding.ItemRiwayatHurufBinding

class RiwayatHurufAdapter :
    RecyclerView.Adapter<RiwayatHurufAdapter.RecentAdapterViewHolder>() {
    inner class RecentAdapterViewHolder(val view: View) :
        RecyclerView.ViewHolder(view)

    private val diffCallback = object : DiffUtil.ItemCallback<Abjad>() {
        override fun areItemsTheSame(oldItem: Abjad, newItem: Abjad): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Abjad, newItem: Abjad): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(list: ArrayList<Abjad>) {

        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapterViewHolder {

        val binding =
            ItemRiwayatHurufBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int) {
        holder.view.apply {
            val data = differ.currentList[position]
            val report = data.reportHuruf
            Log.d("TAG", "onBindViewHolder: $data")
            val binding = ItemRiwayatHurufBinding.bind(this)
            binding.txtAbjad.text = data?.abjadNonKapital
//            binding.imgChecklist.invisible()
            binding.apply {
                imgMateriKapital.setImageDrawable(
                     ContextCompat.getDrawable(
                        context,
                        if (report?.materiHurufKapital == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                )

                imgMateriNonKapital.setImageDrawable((
                     ContextCompat.getDrawable(
                        context,
                        if (report?.materiHurufNonKapital == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                        ))

                imgMateriPerbedaan.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        if (report?.materiPerbedaanHuruf == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                )

                imgQuizKapital.setImageDrawable(
                     ContextCompat.getDrawable(
                        context,
                        if (report?.quizHurufKapital == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                )

                imgQuizNonKapital.setImageDrawable(
                     ContextCompat.getDrawable(
                        context,
                        if (report?.quizHurufNonKapital == true) R.drawable.ic_finished else R.drawable.ic_unfinished
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}

