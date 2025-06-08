package com.nara.bacayuk.di

import com.nara.bacayuk.ui.feat_auth.forgot_password.ForgotPasswordViewModel
import com.nara.bacayuk.ui.feat_auth.login.LoginViewModel
import com.nara.bacayuk.ui.feat_auth.register.RegisterViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf.MenuBacaHurufViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf.QuizBacaHurufViewModel
import com.nara.bacayuk.ui.feat_baca_kata.quiz.QuizViewModel
import com.nara.bacayuk.ui.feat_menu_utama.MainViewModel
import com.nara.bacayuk.ui.feat_riwayat.huruf.RiwayatViewModel
import com.nara.bacayuk.ui.feat_student.add_edit_student.AddEditStudentViewModel
import com.nara.bacayuk.ui.feat_student.list_student.ListStudentViewModel
import com.nara.bacayuk.writing.letter.menu.MenuLetterViewModel
import com.nara.bacayuk.writing.letter.tracing.capital.TracingLetterCapitalViewModel
import com.nara.bacayuk.writing.letter.tracing.lowercase.TracingLetterLowercaseViewModel
import com.nara.bacayuk.writing.number.animation.NumberAnimationViewModel
import com.nara.bacayuk.writing.number.menu.MenuNumberViewModel
import com.nara.bacayuk.writing.number.tracing.TracingNumberViewModel
import com.nara.bacayuk.writing.word.menu.MenuWordViewModel
import com.nara.bacayuk.writing.word.tracing.TracingWordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get(),get(), get()) }
    viewModel { LoginViewModel(get(),get()) }
    viewModel { RegisterViewModel(get(),get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { ListStudentViewModel(get(), get(), get()) }
    viewModel { AddEditStudentViewModel(get(), get()) }
    viewModel { MateriBacaHurufViewModel(get(), get(), get()) }
    viewModel { MenuBacaHurufViewModel(get(), get(), get()) }
    viewModel { QuizBacaHurufViewModel(get(), get(), get()) }
    viewModel { RiwayatViewModel(get(), get(), get()) }
    viewModel { QuizViewModel(get(), get()) }

    viewModel { TracingNumberViewModel(get(), get(), get()) }
    viewModel { NumberAnimationViewModel(get(), get(), get()) }
    viewModel { MenuNumberViewModel(get(), get(), get()) }

    viewModel { MenuLetterViewModel(get(), get(), get()) }
    viewModel { TracingLetterCapitalViewModel(get(), get(), get()) }
    viewModel { TracingLetterLowercaseViewModel(get(), get(), get()) }

    viewModel { MenuWordViewModel(get(), get(), get()) }
    viewModel { TracingWordViewModel(get(), get(), get()) }
}