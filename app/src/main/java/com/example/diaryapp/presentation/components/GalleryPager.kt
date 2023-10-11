package com.example.diaryapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.diaryapp.presentation.screens.write.DiaryState
import com.example.diaryapp.util.GalleryState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryPager(
    galleryState: GalleryState
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val pagerState = rememberPagerState(initialPage = 0)
        val context = LocalContext.current

        HorizontalPager(
            pageCount = galleryState.images.size,
            state = pagerState
        ) { page ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .data(galleryState.images[page].image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}