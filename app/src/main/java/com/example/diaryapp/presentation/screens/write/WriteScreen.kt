package com.example.diaryapp.presentation.screens.write

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.diaryapp.model.GalleryImage
import com.example.diaryapp.model.remote.Diary
import com.example.diaryapp.model.remote.Mood
import com.example.diaryapp.presentation.components.ChooseMoodIconDialog
import com.example.diaryapp.presentation.components.GalleryUploader
import com.example.diaryapp.presentation.components.TopBarWrite
import com.example.diaryapp.presentation.components.ZoomableImage
import com.example.diaryapp.util.GalleryState
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    diaryState: DiaryState,
    galleryState: GalleryState,
    navigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onMoodIconChanged: (Mood) -> Unit,
    onDateTimeUpdated: (ZonedDateTime?) -> Unit,
    onDeleteConfirmClicked: () -> Unit,
    onSaveClicked: (Diary) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageDeleteClicked: (GalleryImage) -> Unit
) {
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }

    BackHandler(selectedGalleryImage != null) {
        selectedGalleryImage = null
    }

    if (selectedGalleryImage == null) {
        Scaffold(
            topBar = {
                TopBarWrite(
                    selectedDiary = diaryState.selectedDiary,
                    navigateBack = navigateBack,
                    onDeleteConfirmClicked = onDeleteConfirmClicked,
                    onDateTimeUpdated = onDateTimeUpdated
                )
            },
            content = { paddingValues ->
                WriteContent(
                    diaryState = diaryState,
                    galleryState = galleryState,
                    onTitleChanged = onTitleChanged,
                    onDescriptionChanged = onDescriptionChanged,
                    onMoodIconChanged = onMoodIconChanged,
                    paddingValues = paddingValues,
                    onSaveClicked = onSaveClicked,
                    onImageSelected = onImageSelected,
                    onImageClicked = { selectedGalleryImage = it }
                )
            }
        )
    } else {
        AnimatedVisibility(visible = true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                selectedGalleryImage?.let {
                    ZoomableImage(
                        selectedGalleryImage = it,
                        onCloseClicked = { selectedGalleryImage = null },
                        onDeleteClicked = {
                            if (selectedGalleryImage != null) {
                                onImageDeleteClicked(selectedGalleryImage!!)
                                selectedGalleryImage = null
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WriteContent(
    diaryState: DiaryState,
    galleryState: GalleryState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onMoodIconChanged: (Mood) -> Unit,
    paddingValues: PaddingValues,
    onSaveClicked: (Diary) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(
                top = paddingValues.calculateTopPadding()
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
                            onNext = {
                                scope.launch {
                                    scrollState.animateScrollTo(Int.MAX_VALUE)
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            }
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
                            onNext = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(12.dp))
            GalleryUploader(
                galleryState = galleryState,
                imageSize = 40.dp,
                onAddClicked = { focusManager.clearFocus() },
                onImageSelected = onImageSelected,
                onImageClicked = onImageClicked
            )
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
                                this.images =
                                    galleryState.images.map { it.remoteImagePath }.toRealmList()
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