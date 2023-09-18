package com.example.diaryapp.presentation.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Shapes
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
import com.example.diaryapp.model.Mood
import com.example.diaryapp.util.Elevation

@Composable
fun ChooseMoodIconDialog(
    isDialogOpened: Boolean,
    onIconChosen: (Int) -> Unit,
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes().extraSmall,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    elevation = CardDefaults.elevatedCardElevation(Elevation.Level1),
                ) {
                    ChooseMoodIconContent(
                        onIconChosen = onIconChosen,
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
    resource: Int,
    contentDescription: String?,
    onIconChosen: (Int) -> Unit,
    onDialogClosed: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))

        if (resource != 0) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onIconChosen(resource)
                        onDialogClosed()
                    },
                painter = painterResource(id = resource),
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
    onIconChosen: (Int) -> Unit,
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
                resource = Mood.Angry.icon,
                contentDescription = "Angry Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Confounded.icon,
                contentDescription = "Confounded Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Cool.icon,
                contentDescription = "Cool Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Crying.icon,
                contentDescription = "Crying Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Dead.icon,
                contentDescription = "Dead Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Disappointed.icon,
                contentDescription = "Disappointed Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Discouraged.icon,
                contentDescription = "Discouraged Icon",
                onIconChosen = onIconChosen,
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
                resource = Mood.Disgusted.icon,
                contentDescription = "Disgusted Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryDisgusted.icon,
                contentDescription = "Very Disgusted Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Funny.icon,
                contentDescription = "Funny Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Happy.icon,
                contentDescription = "Happy Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.VeryHappy.icon,
                contentDescription = "Very Happy Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.LovingIt.icon,
                contentDescription = "Loving It Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Neutral.icon,
                contentDescription = "Neutral Icon",
                onIconChosen = onIconChosen,
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
                resource = Mood.Stressed.icon,
                contentDescription = "Stressed Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Surprised.icon,
                contentDescription = "Surprised Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.Suspicious.icon,
                contentDescription = "Suspicious Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = Mood.TiredBored.icon,
                contentDescription = "Tired/Bored Icon",
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = 0,
                contentDescription = null,
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = 0,
                contentDescription = null,
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
            MoodIcon(
                resource = 0,
                contentDescription = null,
                onIconChosen = onIconChosen,
                onDialogClosed = onDialogClosed
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onDialogClosed,
            shape = Shapes().extraSmall,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.tertiary
            ),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary)
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