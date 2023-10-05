package com.example.diaryapp.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
    fun observe(): Flow<Status>
}