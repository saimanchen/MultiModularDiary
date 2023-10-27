package com.example.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AuthenticationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    fun onAction(action: AuthenticationAction) {
        when (action) {
            is AuthenticationAction.SetIsAuthenticated -> updateIsAuthenticated()
            is AuthenticationAction.SetIsLoading -> updateIsLoading(action.boolean)
            is AuthenticationAction.SignInWithMongoAtlas -> {
                signInWithMongoAtlas(
                    action.tokenId,
                    action.onSuccess,
                    action.onError
                )
            }
        }
    }

    private fun updateIsAuthenticated() {
        _uiState.update { it.copy(isAuthenticated = true) }
    }

    private fun updateIsLoading(boolean: Boolean) {
        _uiState.update { it.copy(isLoading = boolean) }
    }

    private fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    App.create(APP_ID).login(
                        Credentials.jwt(tokenId)
                        //Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                withContext(Dispatchers.Main) {
                    if (result) {
                        onSuccess()
                        delay(600)
                        onAction(AuthenticationAction.SetIsAuthenticated)
                    } else {
                        onError(Exception("User is not logged in."))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}