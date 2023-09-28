package com.example.diaryapp.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.diaryapp.util.Constants.IMAGE_TO_DELETE_TABLE

@Entity(tableName = IMAGE_TO_DELETE_TABLE)
data class ImageToDelete(
    @PrimaryKey
    val id: Int = 0,
    val remoteImagePath: String
)
