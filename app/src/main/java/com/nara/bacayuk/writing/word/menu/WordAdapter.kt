package com.nara.bacayuk.writing.word.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.data.model.ReportTulisKata // Import ReportTulisKata
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ItemAbjadMenuBinding // Asumsi layout item tetap sama
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.visible

// Tambahkan callback untuk long click
class WordAdapter(
    private val listener: AdapterListener,
    private val longClickListener: (ReportTulisKata) -> Unit // Callback untuk long click
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() { // Ganti nama ViewHolder

    inner class WordViewHolder(val binding: ItemAbjadMenuBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Tulis>() {
        override fun areItemsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            // Sekarang Tulis.id adalah ID unik dari Firestore
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem == newItem // Biarkan default comparison jika Tulis adalah data class
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    // submitData sekarang menerima ArrayList<Tulis>
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
            // Anda bisa menampilkan level kata di sini jika mau, misal:
            // txtLevel.text = dataTulis?.reportTulisKata?.level

            // Logika untuk imgChecklist (jika masih relevan)
            imgChecklist.invisible() // Default
            dataTulis?.reportTulisKata?.let { report ->
                if (report.materiTulisKata && report.latihanTulisKata) {
                    imgChecklist.visible()
                } else {
                    imgChecklist.invisible()
                }
            }

            // Click listener biasa untuk membuka tracing
            root.setOnClickListener {
                listener.onClick(dataTulis, position, holder.itemView, "word_trace")
            }

            // Long click listener untuk aksi CRUD (Edit/Delete)
            root.setOnLongClickListener {
                dataTulis?.reportTulisKata?.let { report ->
                    longClickListener.invoke(report) // Kirim ReportTulisKata yang berisi ID unik
                }
                true // Mengindikasikan bahwa event long click sudah di-handle
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
