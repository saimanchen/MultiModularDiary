package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.R
import com.example.util.Elevation
import com.example.util.fetchImagesFromFirebase
import com.example.util.model.Diary
import com.example.util.model.Mood
import com.example.util.toInstant
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun DiaryContainer(
    diary: Diary,
    onClick: (String) -> Unit
) {
    val localDensity = LocalDensity.current
    val context = LocalContext.current
    var componentHeight by remember { mutableStateOf(0.dp) }
    var showContent by remember { mutableStateOf(false) }
    var isGalleryLoading by remember { mutableStateOf(false) }
    val downloadedImages = remember { mutableStateListOf<Uri>() }
    val downloadImageFailedMessage = stringResource(id = R.string.on_image_download_failed)

    LaunchedEffect(key1 = showContent) {
        if (showContent && downloadedImages.isEmpty()) {
            isGalleryLoading = true
            fetchImagesFromFirebase(
                remoteImagePaths = diary.images,
                onImageDownload = { image ->
                    downloadedImages.add(image)
                },
                onImageDownloadFailed = {
                    Toast.makeText(
                        context,
                        downloadImageFailedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    isGalleryLoading = false
                    showContent = true
                },
                onReadyToDisplay = {
                    isGalleryLoading = false
                    showContent = true
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) { onClick(diary._id.toHexString()) }
    ) {
        Surface(
            modifier = Modifier
                .width(1.dp)
                .height(componentHeight + 14.dp)
                .alpha(0.7f),
            color = MaterialTheme.colorScheme.primary
        ) {}
        Spacer(modifier = Modifier.width(14.dp))
        Surface(
            modifier = Modifier
                .border(BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary))
                .onGloballyPositioned {
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = Elevation.Level1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                DiaryEntryHeader(
                    showContent = showContent,
                    onShowContentClicked = { showContent = !showContent },
                    isGallery = diary.images.isNotEmpty(),
                    moodName = diary.mood,
                    time = diary.date.toInstant()
                )
                Column(modifier = Modifier.padding(vertical = 7.dp)) {
                    Text(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 14.dp),
                        text = diary.title,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Light
                        ),
                    )
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn() + expandVertically(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = 300,
                                easing = LinearEasing
                            )
                        )
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .padding(start = 14.dp)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 14.dp),
                                text = diary.description,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = FontWeight.Light
                                ),
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Box(modifier = Modifier.padding(14.dp)) {
                                Gallery(
                                    isGalleryLoading = isGalleryLoading,
                                    images = downloadedImages
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryEntryHeader(
    showContent: Boolean,
    onShowContentClicked: () -> Unit,
    isGallery: Boolean,
    moodName: String,
    time: Instant
) {
    val mood by remember { mutableStateOf(Mood.valueOf(moodName)) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = mood.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (isGallery) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_photo_24),
                        contentDescription = "Show More",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = SimpleDateFormat("hh:mm a", Locale.GERMAN).format(Date.from(time)),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onShowContentClicked() },
                    imageVector = if (showContent) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (showContent) "Hide Content" else "Show Content"
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}