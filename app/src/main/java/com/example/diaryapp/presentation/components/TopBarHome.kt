package com.example.diaryapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@ExperimentalMaterial3Api
@Composable
fun TopBarHome(
    isDateSelected: Boolean,
    onDateSelected: (ZonedDateTime) -> Unit,
    onDateResetSelected: () -> Unit,
    onLogOutClicked: () -> Unit,
    onDeleteAllDiaryEntriesClicked: () -> Unit
) {
    val dateDialogState = rememberUseCaseState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    TopAppBar(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navigationIcon = {
            IconButton(onClick = onLogOutClicked) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out"
                )
            }
        },
        title = {
            Text(text = "")
        },
        actions = {
            if (isDateSelected) {
                IconButton(onClick = onDateResetSelected) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Unselect filtered date"
                    )
                }
            } else {
                IconButton(onClick = { dateDialogState.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date"
                    )
                }
            }
            IconButton(onClick = onDeleteAllDiaryEntriesClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete All Diary Entries"
                )
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
            selectedDate = localDate
            onDateSelected(
                ZonedDateTime.of(
                    selectedDate,
                    LocalTime.now(),
                    ZoneId.systemDefault()
                )
            )
        },
        config = CalendarConfig(monthSelection = true, yearSelection = true)
    )
}