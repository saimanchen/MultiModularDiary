package com.example.diaryapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
                .border(BorderStroke(width = 1.dp, brush = SolidColor(Color.LightGray))),
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold

                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal
                )
            },
            shape = RoundedCornerShape(10.dp),
            containerColor = MaterialTheme.colorScheme.background,
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClicked()
                        onCloseDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(1.dp),
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onCloseDialog,
                    shape = RoundedCornerShape(1.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = BorderStroke(width = 1.dp, brush = SolidColor(Color.LightGray) )
                ) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = onCloseDialog
        )
    }
}