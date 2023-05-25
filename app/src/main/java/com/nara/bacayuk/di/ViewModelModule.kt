package com.nara.bacayuk.di

import com.nara.bacayuk.ui.feat_auth.forgot_password.ForgotPasswordViewModel
import com.nara.bacayuk.ui.feat_auth.login.LoginViewModel
import com.nara.bacayuk.ui.feat_auth.register.RegisterViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf.MateriBacaHurufViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf.MenuBacaHurufViewModel
import com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf.QuizBacaHurufViewModel
import com.nara.bacayuk.ui.feat_menu_utama.MainViewModel
import com.nara.bacayuk.ui.feat_student.add_edit_student.AddEditStudentViewModel
import com.nara.bacayuk.ui.feat_student.list_student.ListStudentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(),get()) }
    viewModel { RegisterViewModel(get(),get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { ListStudentViewModel(get(), get(), get()) }
    viewModel { AddEditStudentViewModel(get(), get()) }
    viewModel { MateriBacaHurufViewModel(get(), get(), get()) }
    viewModel { MenuBacaHurufViewModel(get(), get(), get()) }
    viewModel { QuizBacaHurufViewModel(get(), get(), get()) }
}