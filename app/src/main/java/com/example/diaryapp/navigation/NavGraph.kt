package com.example.diaryapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auth.navigation.authenticationRoute
import com.example.diaryapp.presentation.screens.home.HomeScreen
import com.example.diaryapp.presentation.screens.home.HomeViewModel
import com.example.diaryapp.presentation.screens.write.WriteScreen
import com.example.diaryapp.presentation.screens.write.WriteViewModel
import com.example.ui.components.CustomAlertDialog
import com.example.util.Constants.APP_ID
import com.example.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.util.RequestState
import com.example.util.Screen
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

fun NavGraphBuilder.homeRoute(
    navigateToAuthentication: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val diaryEntries by viewModel.diaryEntries
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var isSignOutDialogOpened by remember { mutableStateOf(false) }
        var isDeleteAllDiaryEntriesDialogOpened by remember { mutableStateOf(false) }


        LaunchedEffect(key1 = diaryEntries) {
            if (diaryEntries != RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaryEntries = diaryEntries,
            isDateSelected = viewModel.isDateSelected,
            onDateSelected = {
                viewModel.getDiaryEntries(zonedDateTime = it)
            },
            onDateResetSelected = { viewModel.getDiaryEntries() },
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
        val context = LocalContext.current
        val viewModel: WriteViewModel = hiltViewModel()
        val diaryState = viewModel.diaryState
        val galleryState = viewModel.galleryState

        LaunchedEffect(key1 = diaryState, block = {
            Log.d("DiaryId", "${diaryState.selectedDiaryId}")
        })

        WriteScreen(
            diaryState = diaryState,
            galleryState = galleryState,
            navigateBack = navigateBack,
            onTitleChanged = { viewModel.setTitle(it) },
            onDescriptionChanged = { viewModel.setDescription(it) },
            onMoodIconChanged = { viewModel.setMood(mood = it) },
            onDeleteConfirmClicked = {
                viewModel.deleteDiaryEntry(
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
                )
            },
            onDateTimeUpdated = { viewModel.setDateTime(it) },
            onSaveClicked = {
                viewModel.upsertDiary(
                    diary = it,
                    onSuccess = navigateBack,
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onImageSelected = {
                val imageType = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"

                viewModel.generateImagePathAndAddToGalleryStateList(
                    image = it,
                    imageType = imageType
                )
            },
            onImageDeleteClicked = { galleryState.deleteImage(it) }
        )
    }
}