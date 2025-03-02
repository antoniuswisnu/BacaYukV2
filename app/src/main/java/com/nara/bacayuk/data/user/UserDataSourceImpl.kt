package com.nara.bacayuk.data.user

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserDataSourceImpl(private val firestore: FirebaseFirestore): UserDataSource{

    override fun getUserFromFirestore(id: String): Flow<Response<User>> {

        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users").document(id).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                emit(Response.Success(user)) // jika dapat ditemukan data user dari Firestore
                Log.d("UserDataSourceImpl", "getUserFromFirestore: $user")
            } else {
                emit(Response.Error(null.toString(),"User not found."))
                Log.d("UserDataSourceImpl", "getUserFromFirestore: Notfound")// jika tidak dapat ditemukan data user dari Firestore
            }
        }.catch {
            emit(Response.Error(null.toString(),"Failed to fetch user data from Firestore."))
            Log.e("UserDataSourceImpl", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun addUpdateUserToFirestore(user: User): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val documentReference = user.uuid?.let {
                firestoreInstance.collection("Users").document(
                    it
                )
            }
            documentReference?.set(user)?.await()
            true // kembalikan nilai boolean true jika operasi berhasil
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false // kembalikan nilai boolean false jika operasi gagal
        }
    }


}