package com.driver.drowsiness.detection.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.components.Pulsating
import com.driver.drowsiness.detection.constants.Routes
import com.driver.drowsiness.detection.ui.theme.DarkColor
import com.driver.drowsiness.detection.ui.theme.poppinsFontFamily
import com.driver.drowsiness.detection.utils.capitalizeEachWord
import com.driver.drowsiness.detection.utils.removeCredentials
import com.driver.drowsiness.detection.utils.retrieveCredentials

@Composable
fun UserHomeScreen(navController: NavController) {

    val context = LocalContext.current
    val userDetails = retrieveCredentials(context)

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        var name = ""
        if (userDetails != null) {
            name = capitalizeEachWord(userDetails.name)
        }
        Text(
            text = "Hello", style = TextStyle(
                fontSize = 34.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = DarkColor
            ), modifier = Modifier.padding(start = 15.dp, top = 50.dp)
        )

        Text(
            text = name, style = TextStyle(
                fontSize = 52.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                color = DarkColor
            ), modifier = Modifier.padding(start = 15.dp, top = 0.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    navController.navigate(Routes.USER_DATA_SCREEN)
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkColor),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "My Data",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Button(
                onClick = {
                    removeCredentials(context)
                    navController.navigate(Routes.USER_SIGNIN_SCREEN)
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkColor),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Logout",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(320.dp)
                .aspectRatio(1f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Pulsating {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .aspectRatio(1f)
                        .background(Color(0xFFD8D8D8), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .aspectRatio(1f)
                            .background(color = DarkColor, shape = CircleShape)
                            .clickable {
                                navController.navigate(Routes.MONITOR_SCREEN)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start",
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UserHomeScreenPreview() {
    UserHomeScreen(rememberNavController())
}