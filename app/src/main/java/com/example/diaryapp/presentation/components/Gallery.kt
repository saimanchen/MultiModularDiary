package com.example.diaryapp.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.model.GalleryImage
import com.example.diaryapp.util.Elevation
import com.example.diaryapp.util.GalleryState
import kotlin.math.max

@Composable
fun Gallery(
    isGalleryLoading: Boolean,
    modifier: Modifier = Modifier,
    images: List<Uri>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(1)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                images.size - numberOfVisibleImages.value
            }
        }

        Row {
            images.take(numberOfVisibleImages.value).forEach { image ->
                if (isGalleryLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(imageSize)
                            .border(
                                BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    AsyncImage(
                        modifier = Modifier
                            .size(imageSize)
                            .border(
                                BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Gallery Image",
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(spaceBetween))
            }

            if (remainingImages.value > 0) {
                RemainingImagesBox(
                    imageSize = imageSize,
                    remainingImages = remainingImages.value
                )
            }
        }
    }
}

@Composable
fun GalleryUploader(
    modifier: Modifier = Modifier,
    galleryState: GalleryState,
    imageSize: Dp,
    spaceBetween: Dp = 12.dp,
    onAddClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { images ->
        images.forEach {
            onImageSelected(it)
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(2)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                galleryState.images.size - numberOfVisibleImages.value
            }
        }

        Row {
            AddImageButton(
                imageSize = imageSize,
                onClick = {
                    onAddClicked()
                    multiplePhotoPicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.width(spaceBetween))
            galleryState.images.take(numberOfVisibleImages.value).forEach { galleryImage ->
                AsyncImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clickable { onImageClicked(galleryImage) },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(galleryImage.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Gallery Image",
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(spaceBetween))
            }

            if (remainingImages.value > 0) {
                RemainingImagesBox(
                    imageSize = imageSize,
                    remainingImages = remainingImages.value,
                )
            }
        }
    }
}

@Composable
fun AddImageButton(
    imageSize: Dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(imageSize),
        onClick = onClick,
        tonalElevation = Elevation.Level1
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Image"
            )
        }
    }
}

@Composable
fun RemainingImagesBox(
    imageSize: Dp,
    remainingImages: Int,
) {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .size(imageSize),
            color = MaterialTheme.colorScheme.primary
        ) {}

        Text(
            text = remainingImages.toString(),
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}