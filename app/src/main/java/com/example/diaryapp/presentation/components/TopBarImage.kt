package com.example.diaryapp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
fun TopBarImage(
    showImageTopBar: Boolean,
    onNavigateBackClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = showImageTopBar,
        enter = fadeIn(
            animationSpec = tween(
                300,
                easing = LinearEasing
            )
        ),
        exit =
            fadeOut(
                animationSpec = tween(
                    300,
                    easing = LinearEasing
                )
            )
    ) {
        TopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            navigationIcon = {
                IconButton(onClick = onNavigateBackClicked) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Navigate Back"
                    )
                }
            }, title = {
                Text(text = "")
            },
            actions = {
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                actionIconContentColor = MaterialTheme.colorScheme.secondary
            )
        )

    }
}