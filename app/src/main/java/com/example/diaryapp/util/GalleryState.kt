package com.example.diaryapp.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.diaryapp.model.GalleryImage

@Composable
fun rememberGalleryState(): GalleryState {
    return remember { GalleryState() }
}

class GalleryState {
    val images = mutableStateListOf<GalleryImage>()
    val imagesToBeDeleted = mutableStateListOf<GalleryImage>()

    fun addImage(galleryImage: GalleryImage) {
        images.add(galleryImage)
    }

    fun deleteImage(galleryImage: GalleryImage) {
        images.remove(galleryImage)
        imagesToBeDeleted.add(galleryImage)
    }
}