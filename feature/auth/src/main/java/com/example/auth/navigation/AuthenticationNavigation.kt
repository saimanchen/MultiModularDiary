package com.example.auth.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.auth.screen.AuthenticationScreen
import com.example.auth.viewmodel.AuthenticationAction
import com.example.auth.viewmodel.AuthenticationViewModel
import com.example.util.Screen
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val onAction = viewModel::onAction
        val state = viewModel.uiState.collectAsState().value
        val authenticatedState = state.isAuthenticated
        val loadingState = state.isLoading
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            authenticatedState = authenticatedState,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.onAction(AuthenticationAction.SetIsLoading(boolean = true))
            },
            onSuccessfulFirebaseSignIn = { tokenId ->
                onAction(AuthenticationAction.SignInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully logged in!")
                        onAction(AuthenticationAction.SetIsLoading(boolean = false))
                    },
                    onError = {
                        messageBarState.addError(it)
                        onAction(AuthenticationAction.SetIsLoading(boolean = false))
                    }
                ))
            },
            onFailedFirebaseSignIn = {
                messageBarState.addError(it)
                onAction(AuthenticationAction.SetIsLoading(boolean = false))
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                onAction(AuthenticationAction.SetIsLoading(boolean = false))
            },
            navigateToHome = navigateToHome
        )
    }
}
