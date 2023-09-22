package com.example.diaryapp.data.repository

import com.example.diaryapp.model.Diary
import com.example.diaryapp.util.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias DiaryEntries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoDBRepository {
    fun configureRealm()
    fun getAllDiaryEntries(): Flow<DiaryEntries>
    fun getSelectedDiaryEntry(diaryId: ObjectId): Flow<RequestState<Diary>>
    suspend fun insertDiaryEntry(diary: Diary): RequestState<Diary>
    suspend fun updateDiaryEntry(diary: Diary): RequestState<Diary>
    suspend fun deleteDiaryEntry(id: ObjectId): RequestState<Diary>
}