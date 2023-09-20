package com.example.diaryapp.navigation

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaryapp.model.Diary
import com.example.diaryapp.presentation.components.CustomAlertDialog
import com.example.diaryapp.presentation.screens.authentication.AuthenticationScreen
import com.example.diaryapp.presentation.screens.authentication.AuthenticationViewModel
import com.example.diaryapp.presentation.screens.home.HomeScreen
import com.example.diaryapp.presentation.screens.home.HomeViewModel
import com.example.diaryapp.presentation.screens.write.WriteScreen
import com.example.diaryapp.presentation.screens.write.WriteViewModel
import com.example.diaryapp.util.Constants.APP_ID
import com.example.diaryapp.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.diaryapp.util.RequestState
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.lang.Exception

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToAuthentication = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            onDataLoaded = onDataLoaded,
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(diaryId = it))
            }
        )
        writeRoute(
            navigateBack = {
                navController.popBackStack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticatedState by viewModel.authenticatedState
        val loadingState by viewModel.loadingState
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
                viewModel.setLoading(true)
            },
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully logged in!")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToAuthentication: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val diaryEntries by viewModel.diaryEntries
        val scope = rememberCoroutineScope()
        var isSignOutDialogOpened by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = diaryEntries) {
            if (diaryEntries != RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaryEntries = diaryEntries,
            onLogOutClicked = {
                isSignOutDialogOpened = true
            },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs
        )

        CustomAlertDialog(
            title = "Sign Out",
            message = "Do you want to sign out?",
            isDialogOpened = isSignOutDialogOpened,
            onCloseDialog = { isSignOutDialogOpened = false },
            onConfirmClicked = {
                scope.launch(Dispatchers.IO) {
                    App.create(APP_ID).currentUser?.logOut()

                    withContext(Dispatchers.Main) {
                        navigateToAuthentication()
                    }
                }
            }
        )
    }
}

fun NavGraphBuilder.writeRoute(
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARG_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val viewModel: WriteViewModel = viewModel()
        val diaryState = viewModel.diaryState

        LaunchedEffect(key1 = diaryState, block = {
            Log.d("DiaryId", "${diaryState.selectedDiaryId}")
        })

        WriteScreen(
            diaryState = diaryState,
            selectedDiary = Diary().apply {
                title = "Hello Diary!"
                description =
                    "My day started really bad, but when I saw You, it suddenly was much better."
            },
            navigateBack = navigateBack,
            onTitleChanged = { viewModel.setTitle(it) },
            onDescriptionChanged = { viewModel.setDescription(it) },
            onDeleteConfirmClicked = {}
        )
    }
}