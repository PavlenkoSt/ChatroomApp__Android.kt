package com.learning.chatroomapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.learning.chatroomapp.data.FirebaseRequestResult
import com.learning.chatroomapp.data.Message
import com.learning.chatroomapp.data.User
import com.learning.chatroomapp.repositories.MessageRepository
import com.learning.chatroomapp.repositories.UserRepository
import com.learning.chatroomapp.utils.FirestoreInjection
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {
    private val messageRepository: MessageRepository =
        MessageRepository(FirestoreInjection.instance())

    private val userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirestoreInjection.instance()
    )

    init {
        loadCurrentUser()
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is FirebaseRequestResult.Success -> _currentUser.value = result.data
                is FirebaseRequestResult.Error -> {
                    // Handle error, e.g., show a snackbar
                }
                else -> {}
            }
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            if (_roomId != null) {
                messageRepository.getChatMessages(_roomId.value.toString())
                    .collect { _messages.value = it }
            }
        }
    }

    fun sendMessage(text: String) {
        if (_currentUser.value != null) {
            val message = Message(
                senderFirstName = _currentUser.value!!.firstName,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch {
                when (messageRepository.sendMessage(_roomId.value.toString(), message)) {
                    is FirebaseRequestResult.Success -> Unit
                    is FirebaseRequestResult.Error -> {

                    }
                    else -> {}
                }
            }
        }
    }

    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }
}