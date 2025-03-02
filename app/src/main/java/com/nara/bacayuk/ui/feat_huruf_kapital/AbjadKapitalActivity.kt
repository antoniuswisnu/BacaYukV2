package com.nara.bacayuk.ui.feat_huruf_kapital

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.databinding.ActivityAbjadKapitalBinding
import com.nara.bacayuk.ui.feat_detail_abjad.DetailAbjadActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener

class AbjadKapitalActivity : AppCompatActivity(), AdapterListener {
    private val binding by lazy { ActivityAbjadKapitalBinding.inflate(layoutInflater) }
    private val abjadAdapter by lazy { AbjadAdapter(this@AbjadKapitalActivity) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        abjadAdapter.submitData(getAbjadKapital())
        binding.apply {
            rvListStudent.apply {
                adapter = abjadAdapter
                //flexible grid layout
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@AbjadKapitalActivity, 4)
            }
        }
    }


//    private fun getAbjadKapital(): ArrayList<Abjad?>? {
//        val abjadKapital = ArrayList<Abjad?>()
//        abjadKapital.add(Abjad(0, "A", "#F89F47", "a"))
//        abjadKapital.add(Abjad(1, "B", "#F89F47", "b"))
//        abjadKapital.add(Abjad(2, "C", "#F89F47", "c"))
//        abjadKapital.add(Abjad(3, "D", "#F89F47", "d"))
//        abjadKapital.add(Abjad(4, "E", "#F89F47", "e"))
//        abjadKapital.add(Abjad(5, "F", "#F89F47", "f"))
//        abjadKapital.add(Abjad(6, "G", "#F89F47", "g"))
//        abjadKapital.add(Abjad(7, "H", "#F89F47", "h"))
//        abjadKapital.add(Abjad(8, "I", "#F89F47", "i"))
//        abjadKapital.add(Abjad(9, "J", "#F89F47", "j"))
//        abjadKapital.add(Abjad(10, "K", "#F89F47", "k"))
//        abjadKapital.add(Abjad(11, "L", "#F89F47", "l"))
//        abjadKapital.add(Abjad(12, "M", "#F89F47", "m"))
//        abjadKapital.add(Abjad(13, "N", "#F89F47", "n"))
//        abjadKapital.add(Abjad(14, "O", "#F89F47", "o"))
//        abjadKapital.add(Abjad(15, "P", "#F89F47", "p"))
//        abjadKapital.add(Abjad(16, "Q", "#F89F47", "q"))
//        abjadKapital.add(Abjad(17, "R", "#F89F47", "r"))
//        abjadKapital.add(Abjad(18, "S", "#F89F47", "s"))
//        abjadKapital.add(Abjad(19, "T", "#F89F47", "t"))
//        abjadKapital.add(Abjad(20, "U", "#F89F47", "u"))
//        abjadKapital.add(Abjad(21, "V", "#F89F47", "v"))
//        abjadKapital.add(Abjad(22, "W", "#F89F47", "w"))
//        abjadKapital.add(Abjad(23, "X", "#F89F47", "x"))
//        abjadKapital.add(Abjad(24, "Y", "#F89F47", "y"))
//        abjadKapital.add(Abjad(25, "Z", "#F89F47", "z"))
//        return abjadKapital
//    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        startActivity(Intent(this@AbjadKapitalActivity, DetailAbjadActivity::class.java))
    }
}