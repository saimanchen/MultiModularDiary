package com.example.diaryapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.diaryapp.model.Diary
import com.example.diaryapp.util.toInstant
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWrite(
    selectedDiary: Diary?,
    navigateBack: () -> Unit,
    onDeleteConfirmClicked: () -> Unit
) {
    val currentDate by remember { mutableStateOf(LocalDate.now()) }
    val currentTime by remember { mutableStateOf(LocalTime.now()) }
    val formattedDate = remember(key1 = currentDate) {
        DateTimeFormatter
            .ofPattern("dd MMM yyyy")
            .format(currentDate)
            .uppercase()
    }
    val formattedTime = remember(key1 = currentTime) {
        DateTimeFormatter
            .ofPattern("hh:mm a")
            .format(currentTime)
    }
    val selectedDiaryDateTime = remember(selectedDiary) {
        if (selectedDiary != null) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date.from(selectedDiary.date.toInstant())).uppercase()
        } else {
            "Not Specified"
        }
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = if (selectedDiary != null) selectedDiaryDateTime else "$formattedDate, $formattedTime",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            )
        },
        actions = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Navigate Back"
                )
            }

            if (selectedDiary != null) {
                DeleteDiaryEntryAction(onDeleteConfirmClicked = onDeleteConfirmClicked)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary
        ),
    )
}

@Composable
fun DeleteDiaryEntryAction(
    onDeleteConfirmClicked: () -> Unit
) {
    var isDeleteDialogOpened by remember { mutableStateOf(false) }

    IconButton(onClick = { isDeleteDialogOpened = true }) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Diary Entry"
        )
    }

    CustomAlertDialog(
        title = "Delete Diary Entry",
        message = "Do you want to delete this diary entry?",
        isDialogOpened = isDeleteDialogOpened,
        onCloseDialog = { isDeleteDialogOpened = false },
        onConfirmClicked = onDeleteConfirmClicked
    )
}