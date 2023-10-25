package com.example.write.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.util.Screen
import com.example.write.WriteScreen
import com.example.write.WriteViewModel

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