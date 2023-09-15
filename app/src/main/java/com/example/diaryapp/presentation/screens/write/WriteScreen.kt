package com.example.diaryapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.diaryapp.model.Diary
import com.example.diaryapp.presentation.components.TopBarWrite

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    selectedDiary: Diary?,
    navigateBack: () -> Unit,
    onDeleteConfirmClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarWrite(
                selectedDiary = selectedDiary,
                navigateBack = navigateBack,
                onDeleteConfirmClicked = onDeleteConfirmClicked
            )
        },
        content = {

        }
    )
}

@Composable
fun WriteContent() {

}