package com.example.auth.viewmodel

internal data class AuthenticationUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false
)

internal sealed class AuthenticationAction {
    object SetIsAuthenticated : AuthenticationAction()
    data class SetIsLoading(val boolean: Boolean) : AuthenticationAction()
    data class SignInWithMongoAtlas(
        val tokenId: String,
        val onSuccess: () -> Unit,
        val onError: (Exception) -> Unit
    ) : AuthenticationAction()
}