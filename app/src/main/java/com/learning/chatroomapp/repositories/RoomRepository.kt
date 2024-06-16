package com.learning.chatroomapp.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.learning.chatroomapp.data.Room
import com.learning.chatroomapp.data.FirebaseRequestResult
import com.learning.chatroomapp.data.RoomCreate
import kotlinx.coroutines.tasks.await

class RoomRepository(private val firestore: FirebaseFirestore) {
    suspend fun createRoom(name: String): FirebaseRequestResult<Unit> = try {
        val room = RoomCreate(name = name)
        firestore.collection("rooms").add(room).await()
        FirebaseRequestResult.Success(Unit)
    } catch (e: Exception) {
        FirebaseRequestResult.Error(e)
    }

    suspend fun getRooms(): FirebaseRequestResult<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        FirebaseRequestResult.Success(rooms)
    } catch (e: Exception) {
        FirebaseRequestResult.Error(e)
    }
}
