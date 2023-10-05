package com.example.diaryapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.connectivity.ConnectivityObserver
import com.example.diaryapp.connectivity.NetworkConnectivityObserver
import com.example.diaryapp.data.repository.DiaryEntries
import com.example.diaryapp.data.repository.MongoDB
import com.example.diaryapp.model.local.ImagesToDeleteDao
import com.example.diaryapp.model.local.entity.ImageToDelete
import com.example.diaryapp.util.RequestState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val imagesToDeleteDao: ImagesToDeleteDao
) : ViewModel() {
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)
    var diaryEntries: MutableState<DiaryEntries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaryEntries()
        viewModelScope.launch {
            connectivity.observe().collect {
                network = it
            }
        }
    }

    private fun observeAllDiaryEntries() {
        viewModelScope.launch {
            MongoDB.getAllDiaryEntries().collect { result ->
                diaryEntries.value = result
            }
        }
    }

    fun deleteAllDiaryEntries(
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
                        val result = MongoDB.deleteAllDiaryEntries()
                        if(result is RequestState.Success) {
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