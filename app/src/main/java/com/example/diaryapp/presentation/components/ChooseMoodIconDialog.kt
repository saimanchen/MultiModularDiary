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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diaryapp.R
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
                contentDescription = stringResource(id = R.string.angry_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Confounded,
                contentDescription = stringResource(id = R.string.confounded_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Cool,
                contentDescription = stringResource(id = R.string.cool_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Crying,
                contentDescription = stringResource(id = R.string.crying_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Dead,
                contentDescription = stringResource(id = R.string.dead_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Disappointed,
                contentDescription = stringResource(id = R.string.disappointed_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Discouraged,
                contentDescription = stringResource(id = R.string.discouraged_icon),
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
                contentDescription = stringResource(id = R.string.disgusted_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryDisgusted,
                contentDescription = stringResource(id = R.string.very_disgusted_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Funny,
                contentDescription = stringResource(id = R.string.funny_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Happy,
                contentDescription = stringResource(id = R.string.happy_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryHappy,
                contentDescription = stringResource(id = R.string.very_happy_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.LovingIt,
                contentDescription = stringResource(id = R.string.loving_it_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Neutral,
                contentDescription = stringResource(id = R.string.neutral_icon),
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
                contentDescription = stringResource(id = R.string.stressed_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Surprised,
                contentDescription = stringResource(id = R.string.surprised_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Suspicious,
                contentDescription = stringResource(id = R.string.suspicious_icon),
                onMoodIconChanged = onMoodIconChanged,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.TiredBored,
                contentDescription = stringResource(id = R.string.tired_bored_icon),
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
            shape = RectangleShape,
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.close),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}