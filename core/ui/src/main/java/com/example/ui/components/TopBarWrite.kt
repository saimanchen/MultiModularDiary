package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.example.ui.R
import com.example.util.model.Diary
import com.example.util.toInstant
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWrite(
    selectedDiary: Diary?,
    onDateTimeUpdated: (ZonedDateTime?) -> Unit,
    navigateBack: () -> Unit,
    onDeleteConfirmClicked: () -> Unit
) {
    val dateDialogState = rememberUseCaseState()
    val timeDialogState = rememberUseCaseState()
    var isDateTimeUpdated by remember { mutableStateOf(false) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
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
    val notSpecifiedText = stringResource(id = R.string.not_specified)
    val selectedDiaryDateTime = remember(selectedDiary) {
        if (selectedDiary != null) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date.from(selectedDiary.date.toInstant())).uppercase()
        } else {
            notSpecifiedText
        }
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(id = R.string.navigate_back)
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = if (selectedDiary != null && isDateTimeUpdated) {
                    "$formattedDate, $formattedTime"
                } else if (selectedDiary != null) {
                    selectedDiaryDateTime
                } else {
                    "$formattedDate, $formattedTime"
                },
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            )
        },
        actions = {
            if (isDateTimeUpdated) {
                IconButton(onClick = {
                    currentDate = LocalDate.now()
                    currentTime = LocalTime.now()
                    isDateTimeUpdated = false
                    onDateTimeUpdated(null)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.delete_custom_time)
                    )
                }
            } else {
                IconButton(onClick = { dateDialogState.show() }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(id = R.string.choose_date_time)
                    )
                }
            }

            if (selectedDiary != null) {
                DeleteDiaryEntryAction(onDeleteConfirmClicked = onDeleteConfirmClicked)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
    )
    CalendarDialog(
        state = dateDialogState,
        selection = CalendarSelection.Date { localDate ->
            currentDate = localDate
            timeDialogState.show()
        },
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        properties = DialogProperties()
    )
    ClockDialog(
        state = timeDialogState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            currentTime = LocalTime.of(hours, minutes)
            isDateTimeUpdated = true
            onDateTimeUpdated(
                ZonedDateTime.of(
                    currentDate,
                    currentTime,
                    ZoneId.systemDefault()
                )
            )
        }
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
            contentDescription = stringResource(id = R.string.delete_diary_entry)
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