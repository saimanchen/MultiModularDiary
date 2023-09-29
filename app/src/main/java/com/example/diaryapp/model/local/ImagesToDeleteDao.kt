package com.example.diaryapp.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diaryapp.model.local.entity.ImageToDelete

@Dao
interface ImagesToDeleteDao {
      @Query("SELECT * FROM image_to_delete_table ORDER BY id ASC")
      suspend fun getAllImages(): List<ImageToDelete>

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun addImageToDelete(imageToDelete: ImageToDelete)

      @Query("DELETE FROM image_to_delete_table WHERE id=:imageId")
      suspend fun cleanupImage(imageId: Int)
}