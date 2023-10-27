package com.example.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.home.screen.HomeScreen
import com.example.home.viewmodel.HomeViewModel
import com.example.ui.components.CustomAlertDialog
import com.example.util.Constants.APP_ID
import com.example.util.RequestState
import com.example.util.Screen
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import com.example.home.viewmodel.HomeAction

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
                viewModel.deleteAllDiaryEntries(
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
                )
            }
        )
    }
}