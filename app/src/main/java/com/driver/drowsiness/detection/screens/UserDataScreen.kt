package com.driver.drowsiness.detection.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.driver.drowsiness.detection.ui.theme.DarkColor
import com.driver.drowsiness.detection.ui.theme.poppinsFontFamily

@Composable
fun UserDataScreen() {

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {

        Text(
            text = "Hello", style = TextStyle(
                fontSize = 34.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = DarkColor
            ), modifier = Modifier.padding(start = 10.dp, top = 50.dp)
        )


    }
}

@Preview
@Composable
fun UserDataScreenPreview() {
    UserDataScreen()
}