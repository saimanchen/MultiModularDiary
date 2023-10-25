package com.example.ui

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf

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

data class GalleryImage(
    val image: Uri,
    val remoteImagePath: String = ""
)