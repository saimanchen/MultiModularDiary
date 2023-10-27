package com.example.write.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.GalleryImage
import com.example.ui.GalleryState
import com.example.ui.components.ChooseMoodIconDialog
import com.example.ui.components.GalleryPager
import com.example.ui.components.GalleryUploader
import com.example.ui.components.TopBarImage
import com.example.ui.components.TopBarWrite
import com.example.ui.components.ZoomableImage
import com.example.util.model.Diary
import com.example.util.model.Mood
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import com.example.ui.R
import com.example.write.viewmodel.WriteUiState
import io.realm.kotlin.ext.toRealmList

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun WriteScreen(
    diaryState: WriteUiState,
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
    val scope = rememberCoroutineScope()
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }
    var isGalleryOpened by remember { mutableStateOf(false) }
    var showZoomableImage by remember { mutableStateOf(false) }
    var galleryIndex by remember { mutableIntStateOf(0) }
    var showImageTopBar by remember { mutableStateOf(true) }

    BackHandler(enabled = selectedGalleryImage != null) {
        selectedGalleryImage = null
    }
    BackHandler(enabled = showZoomableImage) {
        showZoomableImage = false
    }

    if (isGalleryOpened && !showZoomableImage) {
        AnimatedVisibility(visible = true) {
            Scaffold(
                topBar = {
                    TopBarImage(
                        showImageTopBar = showImageTopBar,
                        showZoomableImage = showZoomableImage,
                        onNavigateBackClicked = {
                            selectedGalleryImage = null
                            isGalleryOpened = false
                        },
                        onDeleteClicked = {
                            if (selectedGalleryImage != null) {
                                onImageDeleteClicked(selectedGalleryImage!!)
                                selectedGalleryImage =
                                    if (
                                        galleryState.images.isNotEmpty() &&
                                        galleryIndex == galleryState.images.size
                                    ) {
                                        galleryState.images[galleryIndex - 1]
                                    } else if (galleryState.images.isNotEmpty()) {
                                        galleryState.images[galleryIndex]
                                    } else {
                                        null
                                    }
                            }
                            if (galleryState.images.isEmpty()) isGalleryOpened = false
                        }
                    )
                },
                content = { paddingValues ->
                    GalleryPager(
                        paddingValues = paddingValues,
                        galleryState = galleryState,
                        galleryIndex = galleryIndex,
                        onSelectedGalleryImageChanged = {
                            galleryIndex = it
                            selectedGalleryImage = galleryState.images[galleryIndex]
                        },
                        onShowZoomableImageClicked = { showZoomableImage = true }
                    )
                }
            )
        }
    } else if (showZoomableImage) {
        AnimatedVisibility(visible = true) {
            Scaffold(
                topBar = {
                    TopBarImage(
                        showImageTopBar = showImageTopBar,
                        showZoomableImage = showZoomableImage,
                        onNavigateBackClicked = { showZoomableImage = false },
                        onDeleteClicked = {
                            if (selectedGalleryImage != null) {
                                onImageDeleteClicked(selectedGalleryImage!!)
                                selectedGalleryImage =
                                    if (
                                        galleryState.images.isNotEmpty() &&
                                        galleryIndex == galleryState.images.size
                                    ) {
                                        galleryState.images[galleryIndex - 1]
                                    } else if (galleryState.images.isNotEmpty()) {
                                        galleryState.images[galleryIndex]
                                    } else {
                                        null
                                    }
                                showZoomableImage = false
                            }
                            if (galleryState.images.isEmpty()) isGalleryOpened = false
                        }
                    )
                },
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        selectedGalleryImage?.let { galleryImage ->
                            ZoomableImage(
                                selectedGalleryImage = galleryImage,
                                onShowImageTopBar = {
                                    scope.launch {
                                        showImageTopBar = !showImageTopBar
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    } else {
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
                    onImageClicked = { index, galleryImage ->
                        galleryIndex = index
                        selectedGalleryImage = galleryImage
                        isGalleryOpened = true
                    }
                )
            }
        )
    }
}

@Composable
internal fun WriteContent(
    diaryState: WriteUiState,
    galleryState: GalleryState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onMoodIconChanged: (Mood) -> Unit,
    paddingValues: PaddingValues,
    onSaveClicked: (Diary) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (Int, GalleryImage) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val fieldCantBeEmptyToastMessageText = stringResource(id = R.string.fields_cant_be_empty)

    LaunchedEffect(key1 = scrollState) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
            .navigationBarsPadding()
            .padding(top = paddingValues.calculateTopPadding())
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
                    text = stringResource(id = R.string.mood),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
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
                            color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Light
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.tell_me_about_it),
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
                            fieldCantBeEmptyToastMessageText,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
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
internal fun ChangeMoodIconAction(
    diaryState: WriteUiState,
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
        contentDescription = "Choose Mood",
        tint = MaterialTheme.colorScheme.onSurface
    )

    ChooseMoodIconDialog(
        isDialogOpened = isMoodDialogOpened,
        onDialogClosed = { isMoodDialogOpened = false },
        onMoodIconChanged = onMoodIconChanged
    )
}