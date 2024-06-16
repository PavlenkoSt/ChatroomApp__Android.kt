package com.learning.chatroomapp.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.learning.chatroomapp.data.User
import com.learning.chatroomapp.data.FirebaseRequestResult
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
){
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): FirebaseRequestResult<Boolean> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                firstName,
                lastName,
                email
            )
            saveUserToFirestore(user)
            FirebaseRequestResult.Success(true)
        } catch (e: Exception) {
            FirebaseRequestResult.Error(e)
        }

    suspend fun signIn(email: String, password: String): FirebaseRequestResult<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            FirebaseRequestResult.Success(true)
        } catch (e: Exception) {
            FirebaseRequestResult.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun getCurrentUser(): FirebaseRequestResult<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d("user2","$uid")
                FirebaseRequestResult.Success(user)
            } else {
                FirebaseRequestResult.Error(Exception("User data not found"))
            }
        } else {
            FirebaseRequestResult.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        FirebaseRequestResult.Error(e)
    }
}