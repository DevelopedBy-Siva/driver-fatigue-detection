package com.driver.drowsiness.detection.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.driver.drowsiness.detection.ui.theme.DarkColor
import com.driver.drowsiness.detection.ui.theme.SemiLightColor
import com.driver.drowsiness.detection.ui.theme.poppinsFontFamily

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    hide: Boolean = false,
) {
    val visualTransformation = if (hide) PasswordVisualTransformation() else VisualTransformation.None
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            placeholder = {
                Text(text = hint, fontFamily = poppinsFontFamily, fontWeight = FontWeight.Normal, color = SemiLightColor)
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = hint,
                    tint = DarkColor
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            visualTransformation= visualTransformation,
            textStyle = TextStyle.Default.copy(fontFamily = poppinsFontFamily, fontWeight = FontWeight.Normal)
        )
    }
}
