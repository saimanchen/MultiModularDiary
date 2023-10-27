package com.example.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.ImagesToDeleteDao
import com.example.database.entity.ImageToDelete
import com.example.repository.MongoDBRepositoryImpl
import com.example.util.RequestState
import com.example.util.connectivity.ConnectivityObserver
import com.example.util.connectivity.NetworkConnectivityObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val imagesToDeleteDao: ImagesToDeleteDao
) : ViewModel() {
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private lateinit var allDiaryEntriesJob: Job
    private lateinit var filteredDiaryEntriesJob: Job

    init {
        onAction(HomeAction.GetDiaryEntries(null))
        viewModelScope.launch {
            connectivity.observe().collect {
                network = it
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.GetDiaryEntries -> getDiaryEntries(action.zonedDateTime)
            is HomeAction.ObserveAllDiaryEntries -> observeAllDiaryEntries()
            is HomeAction.ObserveFilteredDiaryEntries -> observeFilteredDiaryEntries(action.zonedDateTime)
            is HomeAction.DeleteAllDiaryEntries -> {
                deleteAllDiaryEntries(
                    action.onSuccess,
                    action.onError
                )
            }
        }
    }

    private fun getDiaryEntries(zonedDateTime: ZonedDateTime? = null) {
        _uiState.update { it.copy(
            isDateSelected = zonedDateTime != null,
            diaryEntries = RequestState.Loading
        ) }

        if (uiState.value.isDateSelected && zonedDateTime != null) {
            onAction(HomeAction.ObserveFilteredDiaryEntries(zonedDateTime = zonedDateTime))
        } else {
            onAction(HomeAction.ObserveAllDiaryEntries)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAllDiaryEntries() {
        allDiaryEntriesJob = viewModelScope.launch {
            if (::filteredDiaryEntriesJob.isInitialized) {
                filteredDiaryEntriesJob.cancelAndJoin()
            }
            MongoDBRepositoryImpl.getAllDiaryEntries().debounce(2000).collect { result ->
                _uiState.update { it.copy(diaryEntries = result) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeFilteredDiaryEntries(zonedDateTime: ZonedDateTime) {
        filteredDiaryEntriesJob = viewModelScope.launch {
            if (::allDiaryEntriesJob.isInitialized) {
                allDiaryEntriesJob.cancelAndJoin()
            }
            MongoDBRepositoryImpl.getFilteredDiaryEntries(zonedDateTime = zonedDateTime).debounce(1000).collect { result ->
                _uiState.update { it.copy(diaryEntries = result) }
            }

        }
    }

    private fun deleteAllDiaryEntries(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (network == ConnectivityObserver.Status.Available) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val imagesDirectory = "images/${userId}"
            val storage = FirebaseStorage.getInstance().reference
            storage.child(imagesDirectory)
                .listAll()
                .addOnSuccessListener {
                    it.items.forEach { reference ->
                        val imagePath = "images/${userId}/${reference.name}"
                        storage.child(imagePath).delete()
                            .addOnFailureListener {
                                viewModelScope.launch(Dispatchers.IO) {
                                    imagesToDeleteDao.addImageToDelete(
                                        ImageToDelete(
                                            remoteImagePath = imagePath
                                        )
                                    )
                                }
                            }
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        val result = MongoDBRepositoryImpl.deleteAllDiaryEntries()
                        if (result is RequestState.Success) {
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        } else if (result is RequestState.Error) {
                            onError(result.error)
                        }
                    }
                }
                .addOnFailureListener { onError(it) }
        } else {
            onError(Exception("No internet connection was found."))
        }
    }
}