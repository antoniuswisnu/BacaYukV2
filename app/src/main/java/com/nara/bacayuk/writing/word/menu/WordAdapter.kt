package com.nara.bacayuk.writing.word.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nara.bacayuk.R

class WordAdapter(
    private val context: Context,
    private val words: List<String>
) : BaseAdapter() {

    override fun getCount(): Int = words.size

    override fun getItem(position: Int): Any = words[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_layout, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val word = words[position]

        holder.apply {
            wordText.text = word
            cardView.setOnClickListener {
                wordClickListener?.onWordClick(word)
            }
        }

        return view
    }

    private var wordClickListener: OnWordClickListener? = null

    fun setOnWordClickListener(listener: OnWordClickListener) {
        this.wordClickListener = listener
    }

    interface OnWordClickListener {
        fun onWordClick(word: String)
    }

    private class ViewHolder(view: View) {
        val cardView: ConstraintLayout = view.findViewById(R.id.cardView)
        val wordText: TextView = view.findViewById(R.id.letterText)
    }
}