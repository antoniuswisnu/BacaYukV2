package com.nara.bacayuk.domain.repository

import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import kotlinx.coroutines.flow.Flow

typealias LoginResponse = Response<User>
typealias RegisterResponse = Response<User>

interface AuthRepository {
    suspend fun register(email: String, password: String): Flow<RegisterResponse>
    suspend fun login(email: String, password: String): Flow<LoginResponse>
    suspend fun forgotPassword(email: String)
    suspend fun logOut()
}
