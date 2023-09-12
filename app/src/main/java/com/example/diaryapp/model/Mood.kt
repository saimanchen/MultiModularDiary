package com.example.diaryapp.model

import androidx.compose.ui.graphics.Color
import com.example.diaryapp.R

enum class Mood(
    val icon: Int,
    val containerColor: Color,
    val contentColor: Color
) {
    Angry(
        icon = R.drawable.ic_mood_angry,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Confounded(
        icon = R.drawable.ic_mood_confounded,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Cool(
        icon = R.drawable.ic_mood_cool,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Crying(
        icon = R.drawable.ic_mood_crying,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Dead(
        icon = R.drawable.ic_mood_dead,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Disappointed(
        icon = R.drawable.ic_mood_disappointed,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Discouraged(
        icon = R.drawable.ic_mood_discouraged,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Disgusted(
        icon = R.drawable.ic_mood_disgusted,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    VeryDisgusted(
        icon = R.drawable.ic_mood_very_disgusted,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Funny(
        icon = R.drawable.ic_mood_funny,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Happy(
        icon = R.drawable.ic_mood_happy,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    VeryHappy(
        icon = R.drawable.ic_mood_very_happy,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    LovingIt(
        icon = R.drawable.ic_mood_loving_it,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Neutral(
        icon = R.drawable.ic_mood_neutral,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Stressed(
        icon = R.drawable.ic_mood_stressed,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Surprised(
        icon = R.drawable.ic_mood_surprised,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    Suspicious(
        icon = R.drawable.ic_mood_suspicious,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
    TiredBored(
        icon = R.drawable.ic_mood_tired_bored,
        containerColor = Color.Black,
        contentColor = Color.White
    ),
}