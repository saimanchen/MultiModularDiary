package com.example.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ui.R
import com.example.repository.DiaryEntries
import com.example.ui.components.DateHeader
import com.example.ui.components.DiaryContainer
import com.example.ui.components.EmptyHomeScreen
import com.example.ui.components.TopBarHome
import com.example.util.RequestState
import com.example.util.model.Diary
import java.time.LocalDate
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    diaryEntries: DiaryEntries,
    isDateSelected: Boolean,
    onDateSelected: (ZonedDateTime) -> Unit,
    onDateResetSelected: () -> Unit,
    onLogOutClicked: () -> Unit,
    onDeleteAllDiaryEntriesClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarHome(
                isDateSelected = isDateSelected,
                onDateSelected = onDateSelected,
                onDateResetSelected = onDateResetSelected,
                onLogOutClicked = onLogOutClicked,
                onDeleteAllDiaryEntriesClicked = onDeleteAllDiaryEntriesClicked
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToWrite,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_new_diary_entry)
                )
            }
        },
        content = {
            when (diaryEntries) {
                is RequestState.Idle -> {}
                is RequestState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is RequestState.Success -> {
                    HomeContent(
                        paddingValues = it,
                        diaryEntries = diaryEntries.data,
                        onClick = navigateToWriteWithArgs,
                        isDateSelected = isDateSelected
                    )
                }

                is RequestState.Error -> {
                    EmptyHomeScreen(
                        title = "Error",
                        subtitle = "${diaryEntries.error.message}",
                        isDateSelected = isDateSelected
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    diaryEntries: Map<LocalDate, List<Diary>>,
    onClick: (String) -> Unit,
    isDateSelected: Boolean
) {
    if (diaryEntries.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp)
                .padding(top = paddingValues.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            diaryEntries.forEach { (localDate, entries) ->
                stickyHeader(key = localDate) {
                    DateHeader(localDate = localDate)
                }

                items(
                    items = entries,
                    key = { it._id.toHexString() }
                ) { entry ->
                    DiaryContainer(diary = entry, onClick = onClick)
                }
            }
        }
    } else {
        EmptyHomeScreen(
            diaryEntries = diaryEntries,
            isDateSelected = isDateSelected
        )
    }
}