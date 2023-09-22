package com.example.diaryapp.presentation.screens.write

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.diaryapp.model.Diary
import com.example.diaryapp.model.Mood
import com.example.diaryapp.presentation.components.ChooseMoodIconDialog
import com.example.diaryapp.presentation.components.TopBarWrite
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    diaryState: DiaryState,
    navigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onMoodIconChanged: (Mood) -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onDeleteConfirmClicked: () -> Unit,
    onSaveClicked: (Diary) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarWrite(
                selectedDiary = diaryState.selectedDiary,
                navigateBack = navigateBack,
                onDeleteConfirmClicked = onDeleteConfirmClicked,
                onDateTimeUpdated = onDateTimeUpdated
            )
        },
        content = {
            WriteContent(
                diaryState = diaryState,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onMoodIconChanged = onMoodIconChanged,
                paddingValues = it,
                onSaveClicked = onSaveClicked
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteContent(
    diaryState: DiaryState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onMoodIconChanged: (Mood) -> Unit,
    paddingValues: PaddingValues,
    onSaveClicked: (Diary) -> Unit
) {
    val context = LocalContext.current
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
                ChangeMoodIconAction(
                    diaryState = diaryState,
                    onMoodIconChanged = onMoodIconChanged
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            BoxWithConstraints {
                val max = maxWidth
                Column {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredWidth(max + 16.dp)
                            .offset(x = (-8).dp),
                        value = diaryState.title,
                        onValueChange = onTitleChanged,
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Light
                        ),
                        placeholder = {
                            Text(
                                text = "Title",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Unspecified,
                            disabledIndicatorColor = Color.Unspecified,
                            unfocusedIndicatorColor = Color.Unspecified,
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        ),
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredWidth(max + 16.dp)
                            .offset(x = (-8).dp)
                            .padding(vertical = 0.dp),
                        value = diaryState.description,
                        onValueChange = onDescriptionChanged,
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Light
                        ),
                        placeholder = {
                            Text(
                                text = "Tell me about it!",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Unspecified,
                            disabledIndicatorColor = Color.Unspecified,
                            unfocusedIndicatorColor = Color.Unspecified,
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        )
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (diaryState.title.isNotEmpty() && diaryState.description.isNotEmpty()) {
                        onSaveClicked(
                            Diary().apply {
                                this.title = diaryState.title
                                this.description = diaryState.description
                                this.mood = diaryState.mood.name
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Fields can't be empty",
                            Toast.LENGTH_SHORT
                            ).show()
                    }
                },
                shape = Shapes().extraSmall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    text = "Save",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
    }
}

@Composable
fun ChangeMoodIconAction(
    diaryState: DiaryState,
    onMoodIconChanged: (Mood) -> Unit
) {
    var isMoodDialogOpened by remember { mutableStateOf(false) }

    Icon(
        modifier = Modifier
            .size(24.dp)
            .clickable {
                isMoodDialogOpened = true
            },
        painter = painterResource(id = diaryState.mood.icon),
        contentDescription = "Choose Mood"
    )

    ChooseMoodIconDialog(
        isDialogOpened = isMoodDialogOpened,
        onDialogClosed = { isMoodDialogOpened = false },
        onMoodIconChanged = onMoodIconChanged
    )
}