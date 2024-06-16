package com.learning.chatroomapp.data

data class Message(
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val senderId: String = "",
    val senderFirstName: String = "",
    val isFromCurrentUser: Boolean = false
)
