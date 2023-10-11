package com.example.diaryapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diaryapp.model.remote.Mood

@Composable
fun ChooseMoodIconDialog(
    isDialogOpened: Boolean,
    onMoodIconChanged: (Mood) -> Unit,
    onDialogClosed: () -> Unit
) {
    if (isDialogOpened) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    ChooseMoodIconContent(
                        onMoodIconChanged = onMoodIconChanged,
                        onDialogClosed = onDialogClosed
                    )
                }
            },
            onDismissRequest = onDialogClosed
        )
    }
}

@Composable
fun MoodIcon(
    resource: Mood?,
    contentDescription: String?,
    onMoodIconChanged: (Mood) -> Unit,
    onDialogClosed: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))

        if (resource != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onMoodIconChanged(resource)
                        onDialogClosed()
                    },
                painter = painterResource(id = resource.icon),
                contentDescription = contentDescription
            )
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
fun ChooseMoodIconContent(
    onMoodIconChanged: (Mood) -> Unit,
    onDialogClosed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Mood",
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Light
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoodIcon(
                resource = Mood.Angry,
                contentDescription = "Angry Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Confounded,
                contentDescription = "Confounded Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Cool,
                contentDescription = "Cool Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Crying,
                contentDescription = "Crying Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Dead,
                contentDescription = "Dead Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Disappointed,
                contentDescription = "Disappointed Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Discouraged,
                contentDescription = "Discouraged Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoodIcon(
                resource = Mood.Disgusted,
                contentDescription = "Disgusted Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryDisgusted,
                contentDescription = "Very Disgusted Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Funny,
                contentDescription = "Funny Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Happy,
                contentDescription = "Happy Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryHappy,
                contentDescription = "Very Happy Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.LovingIt,
                contentDescription = "Loving It Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Neutral,
                contentDescription = "Neutral Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoodIcon(
                resource = Mood.Stressed,
                contentDescription = "Stressed Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Surprised,
                contentDescription = "Surprised Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Suspicious,
                contentDescription = "Suspicious Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.TiredBored,
                contentDescription = "Tired/Bored Icon",
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = null,
                contentDescription = null,
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = null,
                contentDescription = null,
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = null,
                contentDescription = null,
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onDialogClosed,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Close",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}