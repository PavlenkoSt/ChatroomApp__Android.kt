package com.learning.chatroomapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.learning.chatroomapp.repositories.UserRepository
import com.learning.chatroomapp.data.FirebaseRequestResult
import com.learning.chatroomapp.utils.FirestoreInjection
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirestoreInjection.instance()
    )

    private var _authResult = MutableLiveData<FirebaseRequestResult<Boolean>>()
    val authResult: LiveData<FirebaseRequestResult<Boolean>> get() = _authResult

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            val result = userRepository.signUp(email, password, firstName, lastName)
            _authResult.value = result
        }
    }

    fun sighIn(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.signIn(email, password)
            _authResult.value = result
        }
    }

}