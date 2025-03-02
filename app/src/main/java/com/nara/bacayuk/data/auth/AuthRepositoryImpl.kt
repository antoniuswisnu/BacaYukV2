package com.nara.bacayuk.data.auth

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.domain.repository.AuthRepository
import com.nara.bacayuk.domain.repository.LoginResponse
import com.nara.bacayuk.domain.repository.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun logOut() {
        firebaseAuthDataSource.logOut()
    }

    override suspend fun register(email: String, password: String):  Flow<RegisterResponse>{
        return flow {
            try {
                val result: AuthResult = firebaseAuthDataSource.register(email, password)
                if (result.user != null) {
                    Log.d("AuthRepositoryImpl", "register: success")
                    val user = User(result.user!!.uid, result.user!!.email.orEmpty(), "")
                    emit(Response.Success(user))
                } else {
                    Log.d("AuthRepositoryImpl", "register: fail masuk else user null")
                    emit(Response.Error(Exception("Failed to register: User is null").toString()))
                }
            } catch (exception: Exception) {
                Log.d("AuthRepositoryImpl", "register fail catch: ${exception.message}")
                emit(Response.Error(exception.toString()))
            }
        }
    }
    override suspend fun login(email: String, password: String): Flow<LoginResponse> {
        return flow {
            val result: AuthResult = firebaseAuthDataSource.login(email, password)
            if (result.user != null) {
                val user = User(result.user!!.uid, result.user!!.email.orEmpty(), "")
                emit(Response.Success(user))
            } else {
                emit(Response.Error(Exception("Failed to login: User is null").toString()))
            }
        }
    }
    override suspend fun forgotPassword(email: String) {
        firebaseAuthDataSource.forgotPassword(email)
    }
}