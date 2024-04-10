package com.driver.drowsiness.detection.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.driver.drowsiness.detection.ui.theme.DarkColor
import com.driver.drowsiness.detection.ui.theme.LightColor
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
    val visualTransformation =
        if (hide) PasswordVisualTransformation() else VisualTransformation.None

    val focusedColor = SemiLightColor
    val unfocusedColor = LightColor

    val isFocused = remember { mutableStateOf(false) }

    val borderColor = if (isFocused.value) focusedColor else unfocusedColor

    val focusRequester = remember { FocusRequester() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(54.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 16.dp)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = hint,
            tint = if (isFocused.value) DarkColor else unfocusedColor,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (value.isEmpty()) {
            Text(
                text = hint,
                color = if (isFocused.value) focusedColor else unfocusedColor,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .clickable {
                        focusRequester.requestFocus()
                    }
            )
        }
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },

            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal
            ),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState())
                .onFocusChanged { focusState ->
                    isFocused.value = focusState.isFocused
                }
                .focusRequester(focusRequester)
        )


    }
}
