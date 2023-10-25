package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.GalleryState
import com.example.ui.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryPager(
    paddingValues: PaddingValues,
    galleryState: GalleryState,
    galleryIndex: Int,
    onSelectedGalleryImageChanged: (Int) -> Unit,
    onShowZoomableImageClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        val pagerState = rememberPagerState(initialPage = galleryIndex)

        LaunchedEffect(key1 = pagerState.currentPage, block = {
            onSelectedGalleryImageChanged(pagerState.currentPage)
        })

        Column {
            HorizontalPager(
                modifier = Modifier
                    .weight(1f),
                pageSize = PageSize.Fixed(350.dp),
                pageCount = galleryState.images.size,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 12.dp,
                state = pagerState,
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShowZoomableImageClicked() },
                        model =
                        ImageRequest.Builder(context)
                            .data(galleryState.images[page].image)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(id = R.string.gallery_image),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            PagerNavigationBar(
                scope = scope,
                galleryState = galleryState,
                pagerState = pagerState
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerNavigationBar(
    scope: CoroutineScope,
    galleryState: GalleryState,
    pagerState: PagerState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_left_24),
                contentDescription = stringResource(id = R.string.navigate_to_first_image)
            )
        }
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.navigate_to_previous_image)
            )
        }
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.navigate_to_next_image)
            )
        }
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(galleryState.images.size - 1)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_right_24),
                contentDescription = stringResource(id = R.string.navigate_to_last_image)
            )
        }
    }
}