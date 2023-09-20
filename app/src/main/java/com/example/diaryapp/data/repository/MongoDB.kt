package com.example.diaryapp.data.repository

import com.example.diaryapp.model.Diary
import com.example.diaryapp.util.Constants.APP_ID
import com.example.diaryapp.util.RequestState
import com.example.diaryapp.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDB : MongoDBRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (user != null) {
            val configuration = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { subscription ->
                    add(
                        query = subscription.query<Diary>(query = "ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .build()
            realm = Realm.open(configuration)
        }
    }

    override fun getAllDiaryEntries(): Flow<DiaryEntries> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getSelectedDiary(diaryId: ObjectId): RequestState<Diary> {
        return if (user != null) {
            try {
                val diary = realm.query<Diary>(query = "_id == $0", diaryId).find().first()
                RequestState.Success(data = diary)
            } catch (e: Exception) {
                RequestState.Error(e)
            }
        } else
            RequestState.Error(UserNotAuthenticatedException())
    }
}

private class UserNotAuthenticatedException : Exception("User is not logged in.")