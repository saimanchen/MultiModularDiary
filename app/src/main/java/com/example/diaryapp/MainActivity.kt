package com.example.diaryapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.diaryapp.model.local.ImagesToDeleteDao
import com.example.diaryapp.model.local.ImagesToUploadDao
import com.example.diaryapp.navigation.Screen
import com.example.diaryapp.navigation.SetupNavGraph
import com.example.diaryapp.ui.theme.DiaryAppTheme
import com.example.diaryapp.util.Constants.APP_ID
import com.example.diaryapp.util.retryDeletingImageToFirebase
import com.example.diaryapp.util.retryUploadingImageToFirebase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imagesToUploadDao: ImagesToUploadDao
    @Inject
    lateinit var imagesToDeleteDao: ImagesToDeleteDao
    private var keepSplashScreenOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreenOpened
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseApp.initializeApp(this)
        setContent {
            DiaryAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = {
                        keepSplashScreenOpened = false
                    }
                )
            }
        }
        cleanupCheck(
            scope = lifecycleScope,
            imagesToUploadDao = imagesToUploadDao,
            imagesToDeleteDao = imagesToDeleteDao
        )
    }
}

private fun cleanupCheck(
    scope: CoroutineScope,
    imagesToUploadDao: ImagesToUploadDao,
    imagesToDeleteDao: ImagesToDeleteDao
) {
    scope.launch(Dispatchers.IO) {
        val resultUpload = imagesToUploadDao.getAllImages()
        resultUpload.forEach { imageToUpload ->
            retryUploadingImageToFirebase(
                imageToUpload = imageToUpload,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imagesToUploadDao.cleanupImage(imageId = imageToUpload.id)
                        Log.d("roomDB", "uploaded")
                    }
                }
            )
        }
        val resultDelete = imagesToDeleteDao.getAllImages()
        resultDelete.forEach { imageToDelete ->
            retryDeletingImageToFirebase(
                imageToDelete = imageToDelete,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imagesToDeleteDao.cleanupImage(imageId = imageToDelete.id)
                    }
                }
            )
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser

    return if (user != null && user.loggedIn) Screen.Home.route else Screen.Authentication.route
}