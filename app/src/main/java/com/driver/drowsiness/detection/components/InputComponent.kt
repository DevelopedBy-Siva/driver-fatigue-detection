package com.driver.drowsiness.detection.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(hint) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = hint
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}
