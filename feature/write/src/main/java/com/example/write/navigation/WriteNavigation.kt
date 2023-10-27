package com.example.write.navigation

import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.util.Constants.WRITE_SCREEN_ARG_KEY
import com.example.util.Screen
import com.example.write.screen.WriteScreen
import com.example.write.viewmodel.WriteAction
import com.example.write.viewmodel.WriteViewModel

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