package com.example.diaryapp.presentation.screens.home

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
import androidx.compose.ui.unit.dp
import com.example.diaryapp.data.repository.DiaryEntries
import com.example.diaryapp.model.remote.Diary
import com.example.diaryapp.presentation.components.DateHeader
import com.example.diaryapp.presentation.components.DiaryContainer
import com.example.diaryapp.presentation.components.EmptyHomeScreen
import com.example.diaryapp.presentation.components.TopBarHome
import com.example.diaryapp.util.RequestState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    diaryEntries: DiaryEntries,
    onLogOutClicked: () -> Unit,
    onDeleteAllDiaryEntriesClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarHome(
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
                    contentDescription = "Add new entry"
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
                        onClick = navigateToWriteWithArgs
                    )
                }

                is RequestState.Error -> {
                    EmptyHomeScreen(
                        title = "Error",
                        subtitle = "${diaryEntries.error.message}"
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
    onClick: (String) -> Unit
) {
    if (diaryEntries.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
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
        EmptyHomeScreen()
    }
}