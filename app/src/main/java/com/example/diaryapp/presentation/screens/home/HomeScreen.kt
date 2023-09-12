package com.example.diaryapp.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.diaryapp.presentation.components.HomeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onLogOutClicked: () -> Unit,
    navigateToAuthentication: () -> Unit,
    navigateToWrite: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(onMenuClicked = {
                onLogOutClicked()
                navigateToAuthentication()
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToWrite,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new entry"
                )
            }
        },
        content = {

        }
    )
}

@Composable
fun HomeContent() {

}