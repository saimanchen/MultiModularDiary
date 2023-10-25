package com.example.diaryapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auth.navigation.authenticationRoute
import com.example.diaryapp.presentation.screens.write.WriteScreen
import com.example.diaryapp.presentation.screens.write.WriteViewModel
import com.example.home.navigation.homeRoute
import com.example.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.util.Screen

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