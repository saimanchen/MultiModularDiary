package com.example.diaryapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterial3Api
@Composable
fun TopBarHome(onLogOutClicked: () -> Unit) {
    TopAppBar(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navigationIcon = {
            IconButton(onClick = onLogOutClicked) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out"
                )
            }
        }, title = {
            Text(text = "")
        },
        actions = {
            IconButton(onClick = onLogOutClicked) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date"
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
    )
}