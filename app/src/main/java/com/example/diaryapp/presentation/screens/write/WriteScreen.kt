package com.example.diaryapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diaryapp.R
import com.example.diaryapp.model.Diary
import com.example.diaryapp.presentation.components.ChooseMoodIconDialog
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
            WriteContent(paddingValues = it)
        }
    )
}

@Composable
fun WriteContent(
    paddingValues: PaddingValues
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 24.dp,
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood:",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(modifier = Modifier.width(14.dp))
                ChangeMoodIconAction()
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChangeMoodIconAction() {
    var moodIcon by remember { mutableStateOf(R.drawable.ic_mood_neutral) }
    var isMoodDialogOpened by remember { mutableStateOf(false) }

    Icon(
        modifier = Modifier
            .size(24.dp)
            .clickable {
                isMoodDialogOpened = true
            },
        painter = painterResource(id = moodIcon),
        contentDescription = "Choose Mood"
    )

    ChooseMoodIconDialog(
        isDialogOpened = isMoodDialogOpened,
        onDialogClosed = { isMoodDialogOpened = false },
        onIconChosen = { moodIcon = it }
    )
}