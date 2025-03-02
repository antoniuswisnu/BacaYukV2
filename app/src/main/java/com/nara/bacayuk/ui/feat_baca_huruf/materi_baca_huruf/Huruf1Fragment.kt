package com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.databinding.FragmentHuruf1Binding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.utils.AudioPlayerManager
import com.nara.bacayuk.utils.playAudioFromRawAssetsFileString
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Huruf1Fragment : Fragment() {

    private lateinit var listener: (CharSequence) -> Unit

    private var _binding: FragmentHuruf1Binding? = null
    private var abjad: Abjad? = null
    private val dialog by lazy { context?.waitingDialog() }
    private val materiBacaHurufViewModel: MateriBacaHurufViewModel by viewModel()

    val binding get() = _binding!!
    var reportKata: ReportKata? = null
    var mediaPlayer: MediaPlayer? = MediaPlayer()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHuruf1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user: User? = materiBacaHurufViewModel.getUserDataStore()
        user?.uuid?.let { materiBacaHurufViewModel.getUser(it) }

        abjad = MateriBacaHurufActivity.dataAbjad
        when (param1) {
            "0" -> {
                mediaPlayer = null
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    AudioPlayerManager.stopCurrentlyPlaying()
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ins_huruf_kecil)
                    mediaPlayer?.start()
                }
//                playAudioFromRawAssetsFileString(requireContext(),"ins_huruf_kecil")
                binding.materi.apply {
                    txtAbjad.text = abjad?.abjadNonKapital
                    txtDesc.text = getString(R.string.ini_huruf_kecil)
                    abjad?.reportHuruf?.materiHurufNonKapital = true
                    val reportHuruf = abjad?.reportHuruf
                    imgSound.setOnClickListener {
//                        playAudioFromUrl(abjad?.reportHuruf?.abjadName ?: "")
                        val selectedSoundDrawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_selected_sound
                        )
                        imgSound.setImageDrawable(selectedSoundDrawable)
                        val huruf = "huruf_${abjad?.abjadNonKapital?.toLowerCase()}"
                        playAudioFromRawAssetsFileString(requireContext(),huruf)
                    }
                    materiBacaHurufViewModel.updateReportHuruf(
                        user?.uuid ?: "-",
                        MateriBacaHurufActivity.student?.uuid ?: "-",
                        reportHuruf ?: ReportHuruf()
                    )
                }
            }
            "1" -> {
                mediaPlayer = null
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    AudioPlayerManager.stopCurrentlyPlaying()
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ins_huruf_kapital)
                    mediaPlayer?.start()
                }
                binding.materi.apply {
//                    playAudioFromRawAssetsFileString(requireContext(),"ins_huruf_kapital")
                    txtDesc.text = getString(R.string.ini_huruf_kapital)
                    txtAbjad.text = abjad?.abjadKapital
                    abjad?.reportHuruf?.materiHurufKapital = true
                    val reportHuruf = abjad?.reportHuruf
                    imgSound.setOnClickListener {
                        val selectedSoundDrawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_selected_sound
                        )
                        imgSound.setImageDrawable(selectedSoundDrawable)
                        val huruf = "huruf_${abjad?.abjadNonKapital?.toLowerCase()}"
                        playAudioFromRawAssetsFileString(requireContext(),huruf)
                    }
                    materiBacaHurufViewModel.updateReportHuruf(
                        user?.uuid ?: "-",
                        MateriBacaHurufActivity.student?.uuid ?: "-",
                        reportHuruf ?: ReportHuruf()
                    )
                }
            }
            "2" -> {
                mediaPlayer = null
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                    AudioPlayerManager.stopCurrentlyPlaying()
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ins_beda_besar_kecil)
                    mediaPlayer?.start()
                }
                binding.materi.apply {
//                    playAudioFromRawAssetsFileString(requireContext(),"ins_beda_besar_kecil")
                    txtDesc.text = getString(R.string.ini_perbedaan_huruf)
                    "${abjad?.abjadNonKapital} ${abjad?.abjadKapital}".also { txtAbjad.text = it }
                    abjad?.reportHuruf?.materiPerbedaanHuruf = true
                    imgSound.setOnClickListener {
                        val selectedSoundDrawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_selected_sound
                        )
                        imgSound.setImageDrawable(selectedSoundDrawable)
                        val huruf = "huruf_${abjad?.abjadNonKapital?.toLowerCase()}"
                        playAudioFromRawAssetsFileString(requireContext(),huruf)
                    }
                    val reportHuruf = abjad?.reportHuruf
                    materiBacaHurufViewModel.updateReportHuruf(
                        user?.uuid ?: "-",
                        MateriBacaHurufActivity.student?.uuid ?: "-",
                        reportHuruf ?: ReportHuruf()
                    )
                }
            }
        }

        materiBacaHurufViewModel.getAllReportKataFromFirestore(MateriBacaHurufActivity.student?.uuid ?: "-").apply {
            dialog?.show()
        }

        materiBacaHurufViewModel.reportKatas.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    reportKata = response.data
                    dialog?.dismiss()
                    Log.d("reportabjadparam2", response.data.belajarVokal.toString())
                    when (param2) {
                        "0" -> {
                            binding.materi.apply {
                                playAudioFromRawAssetsFileString(requireContext(),"ins_vokal")
                                txtAbjad.text = "A a"
                                txtDesc.text = getString(R.string.ini_huruf_vokal)
                                reportKata?.belajarVokal?.isADone = true
                                imgSound.setOnClickListener {
                                    val selectedSoundDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_selected_sound
                                    )
                                    imgSound.setImageDrawable(selectedSoundDrawable)
                                    playAudioFromRawAssetsFileString(requireContext(),"huruf_a")
                                }
                                Log.d("abjadparam2", "${MateriBacaHurufActivity.student?.uuid} - ${reportKata?.belajarVokal?.isADone}")

                                materiBacaHurufViewModel.updateReportKata(
                                    MateriBacaHurufActivity.student?.uuid ?: "-",
                                    reportKata ?: ReportKata()
                                )
                            }

                        }
                        "1" -> {
                            binding.materi.apply {
                                playAudioFromRawAssetsFileString(requireContext(),"ins_vokal")
                                txtAbjad.text = "I i"
                                txtDesc.text = getString(R.string.ini_huruf_vokal)
                                imgSound.setOnClickListener {
                                    val selectedSoundDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_selected_sound
                                    )
                                    imgSound.setImageDrawable(selectedSoundDrawable)
                                    playAudioFromRawAssetsFileString(requireContext(),"huruf_i")
                                }
                            }
                            reportKata?.belajarVokal?.isIDone = true

                            materiBacaHurufViewModel.updateReportKata(
                                MateriBacaHurufActivity.student?.uuid ?: "-",
                                reportKata ?: ReportKata()
                            )
                        }
                        "2" -> {

                            binding.materi.apply {

                                playAudioFromRawAssetsFileString(requireContext(),"ins_vokal")
                                txtAbjad.text = "U u"
                                txtDesc.text = getString(R.string.ini_huruf_vokal)
                                imgSound.setOnClickListener {
                                    val selectedSoundDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_selected_sound
                                    )
                                    imgSound.setImageDrawable(selectedSoundDrawable)
                                    playAudioFromRawAssetsFileString(requireContext(),"huruf_u")
                                }
                            }
                            reportKata?.belajarVokal?.isUDone = true
                            materiBacaHurufViewModel.updateReportKata(
                                MateriBacaHurufActivity.student?.uuid ?: "-",
                                reportKata ?: ReportKata()
                            )
                        }
                        "3" -> {
                            binding.materi.apply {
                                playAudioFromRawAssetsFileString(requireContext(),"ins_vokal")
                                txtAbjad.text = "E e"
                                txtDesc.text = getString(R.string.ini_huruf_vokal)
                                imgSound.setOnClickListener {
                                    val selectedSoundDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_selected_sound
                                    )
                                    imgSound.setImageDrawable(selectedSoundDrawable)
                                    playAudioFromRawAssetsFileString(requireContext(),"huruf_e")
                                }
                            }
                            reportKata?.belajarVokal?.isEDone = true
                            materiBacaHurufViewModel.updateReportKata(
                                MateriBacaHurufActivity.student?.uuid ?: "-",
                                reportKata ?: ReportKata()
                            )
                        }
                        "4" -> {
                            binding.materi.apply {
                                playAudioFromRawAssetsFileString(requireContext(),"ins_vokal")
                                txtAbjad.text = "O o"
                                txtDesc.text = getString(R.string.ini_huruf_vokal)
                                imgSound.setOnClickListener {
                                    val selectedSoundDrawable = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_selected_sound
                                    )
                                    imgSound.setImageDrawable(selectedSoundDrawable)
                                    playAudioFromRawAssetsFileString(requireContext(),"huruf_o")
                                }
                            }
                            reportKata?.belajarVokal?.isODone = true
                            materiBacaHurufViewModel.updateReportKata(
                                MateriBacaHurufActivity.student?.uuid ?: "-",
                                reportKata ?: ReportKata()
                            )
                        }
                    }
                }
                else -> {

                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, listener: (CharSequence) -> Unit) =
            Huruf1Fragment().apply {
                this.listener = listener
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }

        @JvmStatic
        fun newInstanceKata(param1: String, listener: (CharSequence) -> Unit) =
            Huruf1Fragment().apply {
                this.listener = listener
                arguments = Bundle().apply {
                    putString(ARG_PARAM2, param1)
                }
            }
    }
}