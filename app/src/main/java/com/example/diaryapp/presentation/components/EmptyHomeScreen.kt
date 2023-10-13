package com.example.diaryapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diaryapp.R
import com.example.diaryapp.model.remote.Diary
import java.time.LocalDate

@Composable
fun EmptyHomeScreen(
    diaryEntries: Map<LocalDate, List<Diary>> = mapOf(),
    title: String = stringResource(id = R.string.empty_diary),
    subtitle: String = stringResource(id = R.string.write_your_first_diary_note),
    isDateSelected: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isDateSelected && diaryEntries.isEmpty()) {
                stringResource(id = R.string.nothing_was_written)
            } else {
                title
            },
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface

        )
        Text(
            text = if (isDateSelected && diaryEntries.isEmpty()) {
                stringResource(id = R.string.choose_another_day)
            } else {
                subtitle
            },
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}