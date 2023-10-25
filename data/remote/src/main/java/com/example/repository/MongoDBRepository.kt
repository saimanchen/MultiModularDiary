package com.example.repository

import com.example.util.RequestState
import com.example.util.model.Diary
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZonedDateTime

typealias DiaryEntries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoDBRepository {
    fun configureRealm()
    fun getAllDiaryEntries(): Flow<DiaryEntries>
    fun getFilteredDiaryEntries(zonedDateTime: ZonedDateTime): Flow<DiaryEntries>
    fun getSelectedDiaryEntry(diaryId: ObjectId): Flow<RequestState<Diary>>
    suspend fun insertDiaryEntry(diary: Diary): RequestState<Diary>
    suspend fun updateDiaryEntry(diary: Diary): RequestState<Diary>
    suspend fun deleteDiaryEntry(id: ObjectId): RequestState<Diary>
    suspend fun deleteAllDiaryEntries(): RequestState<Boolean>
}