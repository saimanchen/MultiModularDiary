package com.example.diaryapp.navigation

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaryapp.presentation.components.CustomAlertDialog
import com.example.diaryapp.presentation.screens.authentication.AuthenticationAction
import com.example.diaryapp.presentation.screens.authentication.AuthenticationScreen
import com.example.diaryapp.presentation.screens.authentication.AuthenticationViewModel
import com.example.diaryapp.presentation.screens.home.HomeAction
import com.example.diaryapp.presentation.screens.home.HomeScreen
import com.example.diaryapp.presentation.screens.home.HomeViewModel
import com.example.diaryapp.presentation.screens.write.WriteAction
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

fun NavGraphBuilder.homeRoute(
    navigateToAuthentication: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val onAction = viewModel::onAction
        val state = viewModel.uiState.collectAsState().value
        val diaryEntriesState = state.diaryEntries
        val isDateSelectedState = state.isDateSelected
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var isSignOutDialogOpened by remember { mutableStateOf(false) }
        var isDeleteAllDiaryEntriesDialogOpened by remember { mutableStateOf(false) }


        LaunchedEffect(key1 = diaryEntriesState) {
            if (diaryEntriesState != RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaryEntries = diaryEntriesState,
            isDateSelected = isDateSelectedState,
            onDateSelected = {
                onAction(HomeAction.GetDiaryEntries(zonedDateTime = it))
            },
            onDateResetSelected = {
                onAction(HomeAction.GetDiaryEntries(null))
            },
            onLogOutClicked = {
                isSignOutDialogOpened = true
            },
            onDeleteAllDiaryEntriesClicked = {
                isDeleteAllDiaryEntriesDialogOpened = true
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
        CustomAlertDialog(
            title = "WARNING!",
            message = "Do you want to permanently delete all diary entries?",
            isDialogOpened = isDeleteAllDiaryEntriesDialogOpened,
            onCloseDialog = { isDeleteAllDiaryEntriesDialogOpened = false },
            onConfirmClicked = {
                onAction(
                    HomeAction.DeleteAllDiaryEntries(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "All diary entries was successfully deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            if (it.message == "No internet connection was found.") "No internet connection was found."
                            else it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
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
        val viewModel: WriteViewModel = hiltViewModel()
        val onAction = viewModel::onAction
        val state = viewModel.uiState.collectAsState().value
        val galleryState = viewModel.galleryState
        val context = LocalContext.current

        WriteScreen(
            diaryState = state,
            galleryState = galleryState,
            navigateBack = navigateBack,
            onTitleChanged = { onAction(WriteAction.SetTitle(it)) },
            onDescriptionChanged = { onAction(WriteAction.SetDescription(it)) },
            onMoodIconChanged = { onAction(WriteAction.SetMood(mood = it)) },
            onDeleteConfirmClicked = {
                onAction(WriteAction.DeleteDiaryEntry(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Diary entry is now deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
            },
            onDateTimeUpdated = { onAction(WriteAction.SetDateTime(it)) },
            onSaveClicked = {
                onAction(WriteAction.UpsertDiaryEntry(
                    diary = it,
                    onSuccess = navigateBack,
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
            },
            onImageSelected = {
                val imageType = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"

                onAction(
                    WriteAction.GenerateImagePathAndAddToGalleryStateList(
                        image = it,
                        imageType = imageType
                    )
                )
            },
            onImageDeleteClicked = { galleryState.deleteImage(it) }
        )
    }
}