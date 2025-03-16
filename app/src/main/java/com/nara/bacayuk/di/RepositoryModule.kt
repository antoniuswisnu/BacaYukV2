package com.nara.bacayuk.di

import com.nara.bacayuk.data.auth.AuthRepositoryImpl
import com.nara.bacayuk.data.report.ReportRepositoryImpl
import com.nara.bacayuk.data.student.StudentRepositoryImpl
import com.nara.bacayuk.data.user.UserRepositoryImpl
import com.nara.bacayuk.domain.repository.AuthRepository
import com.nara.bacayuk.domain.repository.ReportRepository
import com.nara.bacayuk.domain.repository.StudentRepository
import com.nara.bacayuk.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<ReportRepository> { ReportRepositoryImpl(get()) }
}