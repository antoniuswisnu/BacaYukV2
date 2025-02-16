package com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.data.model.Abjad
import com.nara.bacayuk.databinding.ItemAbjadMenuBinding
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.visible

class AbjadMenuAdapter(val listener: AdapterListener) :
    RecyclerView.Adapter<AbjadMenuAdapter.RecentAdapterViewHolder>() {

    inner class RecentAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val diffCallback = object : DiffUtil.ItemCallback<Abjad>() {
        override fun areItemsTheSame(oldItem: Abjad, newItem: Abjad): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Abjad, newItem: Abjad): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    private var typ = "-"

    fun submitData(list: ArrayList<Abjad>, type: String) {
        differ.submitList(list)
        typ = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapterViewHolder {
        val binding =
            ItemAbjadMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int) {
        holder.view.apply {
            val data = differ.currentList[position]
            Log.d("TAG", "onBindViewHolder: $data")
            val binding = ItemAbjadMenuBinding.bind(this)
            binding.txtAbjad.text = data?.abjadNonKapital
            binding.imgChecklist.invisible()
            when (typ) {
                "huruf" -> {
                    if (data?.reportHuruf != null) {
                        if (data.reportHuruf.materiHurufKapital
                            && data.reportHuruf.materiHurufNonKapital
                            && data.reportHuruf.materiPerbedaanHuruf
                            && data.reportHuruf.quizHurufKapital
                            && data.reportHuruf.quizHurufNonKapital) {
                            binding.imgChecklist.visible()
                        } else {
                            binding.imgChecklist.invisible()
                        }
                    }
                }
                "kata" -> {
                    if (data?.belajarSuku != null) {

                        if (data.belajarSuku?.belajarVokal?.isADone == true
                            && data.belajarSuku.belajarVokal.isEDone
                            && data.belajarSuku.belajarVokal.isIDone
                            && data.belajarSuku.belajarVokal.isODone
                            && data.belajarSuku.belajarVokal.isUDone) {
                            binding.imgChecklist.visible()
                        } else {
                            binding.imgChecklist.invisible()
                        }
                    }
                }
            }

            rootView.setOnClickListener {
                listener.onClick(data, position, binding.root, "")
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}

