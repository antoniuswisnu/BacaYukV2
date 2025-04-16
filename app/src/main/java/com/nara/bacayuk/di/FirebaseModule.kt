package com.nara.bacayuk.di

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val firebaseModule = module {
    factory { FirebaseApp.initializeApp(androidContext()) }
    single { FirebaseAuth.getInstance(get()) }
    single { FirebaseFirestore.getInstance() }
}