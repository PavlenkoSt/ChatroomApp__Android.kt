package com.learning.chatroomapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.chatroomapp.data.Room
import com.learning.chatroomapp.repositories.RoomRepository
import com.learning.chatroomapp.data.FirebaseRequestResult
import com.learning.chatroomapp.utils.FirestoreInjection
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val roomRepository: RoomRepository = RoomRepository(FirestoreInjection.instance())

    init {
        loadRooms()
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name)
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()) {
                is FirebaseRequestResult.Success -> _rooms.value = result.data
                is FirebaseRequestResult.Error -> {}
                else -> {}
            }
        }
    }

}