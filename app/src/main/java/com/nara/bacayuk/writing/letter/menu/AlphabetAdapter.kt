package com.nara.bacayuk.writing.letter.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nara.bacayuk.R

class AlphabetAdapter(
    private val context: Context,
    private val letters: List<Char>
) : BaseAdapter() {

    override fun getCount(): Int = letters.size

    override fun getItem(position: Int): Any = letters[position]

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

        val letter = letters[position]

        holder.apply {
            letterText.text = letter.toString()
            cardView.setOnClickListener {
                letterClickListener?.onLetterClick(letter.toString())
            }
        }

        return view
    }

    private var letterClickListener: OnLetterClickListener? = null

    fun setOnLetterClickListener(listener: OnLetterClickListener) {
        this.letterClickListener = listener
    }

    interface OnLetterClickListener {
        fun onLetterClick(letter: String)
    }

    private class ViewHolder(view: View) {
        val cardView: ConstraintLayout = view.findViewById(R.id.cardView)
        val letterText: TextView = view.findViewById(R.id.letterText)
    }
}