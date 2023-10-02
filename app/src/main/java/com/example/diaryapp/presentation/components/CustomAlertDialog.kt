package com.example.diaryapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diaryapp.util.Elevation

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    isDialogOpened: Boolean,
    onCloseDialog: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    if (isDialogOpened) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary))
                .background(MaterialTheme.colorScheme.surface),
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Light

                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = Color.Transparent,
            tonalElevation = Elevation.Level5,
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClicked()
                        onCloseDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(1.dp),
                ) {
                    Text(
                        text = "Confirm",
                        style = TextStyle(
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onCloseDialog,
                    shape = RoundedCornerShape(1.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(
                            MaterialTheme.colorScheme.primary
                        )
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            },
            onDismissRequest = onCloseDialog
        )
    }
}