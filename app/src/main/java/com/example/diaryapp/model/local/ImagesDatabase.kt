package com.example.diaryapp.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.diaryapp.model.local.entity.ImageToUpload

@Database(
    entities = [ImageToUpload::class],
    version = 1,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imageToUploadDao(): ImagesToUploadDao
}