package com.nara.bacayuk.di

import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.data.preferences.DataStoreRepositoryImpl
import org.koin.dsl.module

val preferencesModule = module {
    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
}